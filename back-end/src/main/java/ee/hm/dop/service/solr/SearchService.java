package ee.hm.dop.service.solr;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.dao.UserFavoriteDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SolrSearchResponse;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ee.hm.dop.service.solr.SearchCommandBuilder.clearQuerySearch;
import static ee.hm.dop.service.solr.SearchCommandBuilder.isPhrase;
import static ee.hm.dop.service.solr.SearchCommandBuilder.pickGrouping;
import static ee.hm.dop.service.solr.SearchCommandBuilder.quotify;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

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
    public static final Pattern GROUP_KEY_PATTERN = Pattern.compile(GROUP_MATCH_PATTERN);
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

    public SearchResult search(String query, long start, Long limit, SearchFilter searchFilter) {
        searchFilter.setVisibility(SearchConverter.getSearchVisibility(searchFilter.getRequestingUser()));
        SolrSearchRequest searchRequest = buildSearchRequest(query, searchFilter, start, limit);
        SolrSearchResponse searchResponse = solrEngineService.search(searchRequest);

        // empty query hits every solr index causing massive results
        if (StringUtils.isBlank(query) && searchFilter.isEmptySearch())
            searchResponse.getResponse().setTotalResults(learningObjectDao.findAllNotDeleted());

        Map<String, Response> groups = searchResponse.getGrouped();
        if (groups != null) {
            List<Long> orderIds = getOrderIds(searchRequest);
            return getGroupedSearchResult(groups, orderIds, searchRequest.getGrouping(),
                    searchRequest.getItemLimit(), searchFilter.getRequestingUser());
        }
        Response response = searchResponse.getResponse();
        if (response != null) {
            List<Long> orderIds = new ArrayList<>();
            return getSearchResult(limit, response, orderIds, searchFilter.getRequestingUser());
        }
        return new SearchResult();
    }

    private SolrSearchRequest buildSearchRequest(String queryInput, SearchFilter searchFilter, long firstItem, Long limit) {
        String query = clearQuerySearch(queryInput);
        String solrQuery = SearchConverter.composeQueryString(query, searchFilter);
        String sort = SearchConverter.getSort(searchFilter);
        if (StringUtils.isBlank(query)) searchFilter.setGrouped(false);

        SolrSearchRequest searchRequest = new SolrSearchRequest();
        searchRequest.setSolrQuery(solrQuery);
        searchRequest.setSort(sort);
        searchRequest.setFirstItem(firstItem);
        searchRequest.setItemLimit(limit);
        searchRequest.setGrouping(pickGrouping(query, searchFilter));
        searchRequest.setOriginalQuery(isPhrase(query) ? query : quotify(query));
        return searchRequest;
    }

    private List<Long> getOrderIds(SolrSearchRequest searchRequest) {
        return solrEngineService.limitlessSearch(searchRequest)
                .getResponse().getDocuments().stream()
                .map(Document::getId)
                .collect(Collectors.toList());
    }

    private SearchResult getGroupedSearchResult(Map<String, Response> groups, List<Long> orderIds, SearchGrouping grouping, Long limit, User user) {
        SearchResult searchResult = new SearchResult(new HashMap<>());
        searchResult.setStart(-1);
        Map<String, SearchResult> exactResultGroups = new HashMap<>();
        Map<String, SearchResult> similarResultGroups = new HashMap<>();

        for (Map.Entry<String, Response> group : groups.entrySet()) {
            Matcher groupKeyMatcher = GROUP_KEY_PATTERN.matcher(group.getKey());
            if (!groupKeyMatcher.matches()) continue;
            SearchResult singleGroup = getSearchResult(limit, group.getValue().getGroupResponse(), orderIds, user);

            if (grouping.isPhraseGrouping() && groupKeyMatcher.group(QUERY_FIRST_LETTER).startsWith("\"")) {
                addToResults(exactResultGroups, singleGroup, groupKeyMatcher);
            } else {
                addToResults(similarResultGroups, singleGroup, groupKeyMatcher);
            }

            if (searchResult.getStart() == -1) searchResult.setStart(singleGroup.getStart());
        }

        addResultsTogether(searchResult, exactResultGroups, similarResultGroups);
        return searchResult;
    }

    private void addToResults(Map<String, SearchResult> resultGroups, SearchResult singleGroup, Matcher groupKeyMatcher) {
        String groupType = groupKeyMatcher.group(GROUP_TYPE);
        if (!resultGroups.containsKey(groupType)) resultGroups.put(groupType, new SearchResult(new HashMap<>()));
        long resultsInGroup = singleGroup.getTotalResults();
        long resultsInType = resultGroups.get(groupType).getTotalResults();

        String key = groupKeyMatcher.group(GROUP_FOUND_FROM);
        if (groupType.equals("portfolio") && key.equals("summary")) {
            key = "description";
        }
        resultGroups.get(groupType).getGroups().put(key, singleGroup);
        resultGroups.get(groupType).setTotalResults(resultsInType + resultsInGroup);
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

    private SearchResult getSearchResult(Long limit, Response response, List<Long> orderIds, User user) {
        SearchResult searchResult = new SearchResult();
        List<Document> documents = response.getDocuments();
        List<Searchable> unsortedSearchable = retrieveSearchedItems(documents, user, orderIds);
        List<Searchable> sortedSearchable = sortSearchable(documents, unsortedSearchable);

        searchResult.setStart(response.getStart());
        searchResult.setTotalResults(response.getTotalResults() - documents.size() + sortedSearchable.size());

        if (limit == null || limit > 0) searchResult.setItems(sortedSearchable);
        return searchResult;
    }

    private List<Searchable> retrieveSearchedItems(List<Document> documents, User loggedInUser, List<Long> idsForOrder) {
        List<Long> learningObjectIds = documents.stream().map(Document::getId).collect(Collectors.toList());
        List<ReducedLearningObject> reducedLOs = reducedLearningObjectDao.findAllById(learningObjectIds);
        if (loggedInUser != null) {
            List<Long> favored = userFavoriteDao.returnFavoredLearningObjects(learningObjectIds, loggedInUser);
            reducedLOs.forEach(e -> e.setFavorite(favored.contains(e.getId())));
        }
        if (isNotEmpty(idsForOrder)) {
            reducedLOs.forEach(e -> e.setOrderNr(idsForOrder.indexOf(e.getId())));
        }
        return new ArrayList<>(reducedLOs);
    }

    private List<Searchable> sortSearchable(List<Document> allDocuments, List<Searchable> unsortedSearchable) {
        Map<Long, Searchable> idToSearchable = unsortedSearchable.stream()
                .collect(Collectors.toMap(Searchable::getId, searchable -> searchable));

        return allDocuments.stream()
                .filter(document -> idToSearchable.containsKey(document.getId()))
                .map(document -> idToSearchable.get(document.getId()))
                .collect(Collectors.toList());
    }
}
