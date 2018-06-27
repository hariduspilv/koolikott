package ee.hm.dop.service.solr;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.dao.UserFavoriteDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchService {

    public static final String MATERIAL_TYPE = "material";
    public static final String PORTFOLIO_TYPE = "portfolio";
    public static final String SIMILAR_RESULT = "similar";
    public static final String EXACT_RESULT = "exact";
    public static final String ALL_TYPE = "all";
    public static final List<String> SEARCH_TYPES = Arrays.asList(MATERIAL_TYPE, PORTFOLIO_TYPE, ALL_TYPE);
    public static final String SEARCH_BY_TAG_PREFIX = "tag:";
    public static final String SEARCH_RECOMMENDED_PREFIX = "recommended:";
    public static final String SEARCH_BY_AUTHOR_PREFIX = "author:";
    public static final String AND = " AND ";
    public static final String OR = " OR ";
    public static final String EMPTY = "";

    private static final String GROUP_MATCH_PATTERN = "^(.*?):(.).*\\sAND\\stype:\"(\\w*)\"$";
    private static final int GROUP_FOUND_FROM = 1;
    private static final int QUERY_FIRST_LETTER = 2;
    private static final int GROUP_TYPE = 3;

    @Inject
    private SolrEngineService solrEngineService;
    @Inject
    private UserFavoriteDao userFavoriteDao;
    @Inject
    private ReducedLearningObjectDao reducedLearningObjectDao;
    @Inject
    private LearningObjectDao learningObjectDao;

    private static boolean isPhrase(String query) {
        return query != null && query.split("\\s+").length > 1;
    }

    public SearchResult search(String query, long start, Long limit, SearchFilter searchFilter) {
        searchFilter.setVisibility(SearchConverter.getSearchVisibility(searchFilter.getRequestingUser()));
        SearchRequest searchRequest = buildSearchRequest(query, searchFilter, start, limit);
        SearchResponse searchResponse = solrEngineService.search(searchRequest);

        // empty query hits every solr index causing massive results
        if (StringUtils.isBlank(query) && searchFilter.isEmptySearch())
            searchResponse.getResponse().setTotalResults(learningObjectDao.findAllNotDeleted());

        Map<String, Response> groups = searchResponse.getGrouped();
        if (groups != null) return getGroupedSearchResult(searchRequest, searchFilter, groups);
        Response response = searchResponse.getResponse();
        if (response != null) return getSearchResult(limit, searchFilter, response);
        return new SearchResult();
    }

    private SearchRequest buildSearchRequest(String query, SearchFilter searchFilter, long firstItem, Long limit) {
        String solrQuery = SearchConverter.composeQueryString(query, searchFilter);
        String sort = SearchConverter.getSort(searchFilter);

        SearchRequest searchRequest = new SearchRequest();
        if (StringUtils.isBlank(query)) searchFilter.setGrouped(false);
        searchRequest.setSolrQuery(solrQuery);
        searchRequest.setSort(sort);
        searchRequest.setFirstItem(firstItem);
        searchRequest.setItemLimit(limit);
        searchRequest.setGrouping(pickGrouping(query, searchFilter));
        query = isPhrase(query) ? query : "\"" + query + "\"";
        searchRequest.setOriginalQuery(query);

        return searchRequest;
    }

    private SearchGrouping pickGrouping(String query, SearchFilter searchFilter) {
        if (!searchFilter.isGrouped()) return SearchGrouping.GROUP_NONE;
        if (isPhrase(query)) return SearchGrouping.GROUP_PHRASE;
        else return SearchGrouping.GROUP_SIMILAR;
    }

    private SearchResult getGroupedSearchResult(SearchRequest searchRequest, SearchFilter searchFilter,
                                                Map<String, Response> groups) {
        SearchResult searchResult = new SearchResult(new HashMap<>());
        searchResult.setStart(-1);
        Map<String, SearchResult> exactResultGroups = new HashMap<>();
        Map<String, SearchResult> similarResultGroups = new HashMap<>();
        Pattern groupKeyPattern = Pattern.compile(GROUP_MATCH_PATTERN);
        for (Map.Entry<String, Response> group : groups.entrySet()) {
            Matcher groupKeyMatcher = groupKeyPattern.matcher(group.getKey());
            if (!groupKeyMatcher.matches()) continue;
            SearchResult singleGroup = getSearchResult(searchRequest.getItemLimit(), searchFilter, group.getValue().getGroupResponse());

            if (searchRequest.getGrouping().isPhraseGrouping()
                    && groupKeyMatcher.group(QUERY_FIRST_LETTER).startsWith("\"")) {
                addToResults(exactResultGroups, singleGroup, groupKeyMatcher);
            } else addToResults(similarResultGroups, singleGroup, groupKeyMatcher);

            if (searchResult.getStart() == -1) searchResult.setStart(singleGroup.getStart());
        }
        addResultsTogether(searchResult, exactResultGroups, similarResultGroups);
        return searchResult;
    }

    private void addResultsTogether(SearchResult searchResult,
                                    Map<String, SearchResult> exactResultGroups,
                                    Map<String, SearchResult> similarResultGroups) {
        long totalSimilarResults = sumTotalResults(similarResultGroups);
        if (exactResultGroups.isEmpty()) {
            searchResult.setGroups(similarResultGroups);
            searchResult.setTotalResults(totalSimilarResults);
        } else {
            long totalExactResults = sumTotalResults(exactResultGroups);
            searchResult.getGroups().put(SIMILAR_RESULT, new SearchResult(similarResultGroups, totalSimilarResults));
            searchResult.getGroups().put(EXACT_RESULT, new SearchResult(exactResultGroups, totalExactResults));
            searchResult.setTotalResults(totalSimilarResults + totalExactResults);
        }
    }

    private long sumTotalResults(Map<String, SearchResult> groups) {
        return groups.values().stream().mapToLong(SearchResult::getTotalResults).sum();
    }

    private void addToResults(Map<String, SearchResult> resultGroups, SearchResult singleGroup, Matcher groupKeyMatcher) {
        String groupType = groupKeyMatcher.group(GROUP_TYPE);
        if (!resultGroups.containsKey(groupType)) resultGroups.put(groupType, new SearchResult(new HashMap<>()));
        long resultsInGroup = singleGroup.getTotalResults();
        long resultsInType = resultGroups.get(groupType).getTotalResults();

        resultGroups.get(groupType).getGroups().put(groupKeyMatcher.group(GROUP_FOUND_FROM), singleGroup);
        resultGroups.get(groupType).setTotalResults(resultsInType + resultsInGroup);
    }

    private SearchResult getSearchResult(Long limit, SearchFilter searchFilter, Response response) {
        SearchResult searchResult = new SearchResult();
        List<Document> documents = response.getDocuments();
        List<Searchable> unsortedSearchable = retrieveSearchedItems(documents, searchFilter.getRequestingUser());
        List<Searchable> sortedSearchable = sortSearchable(documents, unsortedSearchable);

        searchResult.setStart(response.getStart());
        searchResult.setTotalResults(response.getTotalResults() - documents.size() + sortedSearchable.size());

        if (limit == null || limit > 0) searchResult.setItems(sortedSearchable);
        return searchResult;
    }

    private List<Searchable> retrieveSearchedItems(List<Document> documents, User loggedInUser) {
        List<Searchable> unsortedSearchable = new ArrayList<>();
        List<Long> learningObjectIds = documents.stream().map(Document::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(learningObjectIds)) return unsortedSearchable;
        reducedLearningObjectDao.findAllById(learningObjectIds).forEach(searchable -> {
            checkIfFavorite(loggedInUser, searchable);
            unsortedSearchable.add(searchable);
        });
        return unsortedSearchable;
    }

    private void checkIfFavorite(User user, ReducedLearningObject searchable) {
        if (user == null) return;
        UserFavorite userFavorite = userFavoriteDao.findFavoriteByUserAndLearningObject(searchable.getId(), user);
        searchable.setFavorite(userFavorite != null);
    }

    private List<Searchable> sortSearchable(List<Document> allDocuments, List<Searchable> unsortedSearchable) {
        Map<Long, Searchable> idToSearchable = unsortedSearchable.stream()
                .collect(Collectors.toMap(Searchable::getId, searchable -> searchable));

        List<Searchable> newList = allDocuments.stream()
                .filter(document -> idToSearchable.containsKey(document.getId()))
                .map(document -> idToSearchable.get(document.getId()))
                .collect(Collectors.toList());
        return newList;
    }
}
