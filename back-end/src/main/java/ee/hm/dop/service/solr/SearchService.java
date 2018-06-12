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
import java.util.stream.Collectors;

public class SearchService {

    public static final String MATERIAL_TYPE = "material";
    public static final String PORTFOLIO_TYPE = "portfolio";
    public static final String ALL_TYPE = "all";
    public static final List<String> SEARCH_TYPES = Arrays.asList(MATERIAL_TYPE, PORTFOLIO_TYPE, ALL_TYPE);
    public static final String SEARCH_BY_TAG_PREFIX = "tag:";
    public static final String SEARCH_RECOMMENDED_PREFIX = "recommended:";
    public static final String SEARCH_BY_AUTHOR_PREFIX = "author:";
    public static final String AND = " AND ";
    public static final String OR = " OR ";
    public static final String EMPTY = "";

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
        SearchRequest searchRequest = buildSearchRequest(query, searchFilter, start, limit);
        SearchResponse searchResponse = solrEngineService.search(searchRequest);

        // empty query hits every solr index causing massive results
        if (StringUtils.isBlank(query) && searchFilter.isEmptySearch())
            searchResponse.getResponse().setTotalResults(learningObjectDao.findAllNotDeleted());

        return handleResult(limit, searchFilter, searchResponse);
    }

    public SearchRequest buildSearchRequest(String query, SearchFilter searchFilter, long firstItem, Long limit) {
        String solrQuery = SearchConverter.composeQueryString(query, searchFilter);
        String sort = SearchConverter.getSort(searchFilter);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setOriginalQuery(query);
        searchRequest.setSolrQuery(solrQuery);
        searchRequest.setSort(sort);
        searchRequest.setFirstItem(firstItem);
        searchRequest.setItemLimit(limit);
        if (searchFilter.isGrouped()) searchRequest.setGrouping(SearchGrouping.GROUP_ALL);
        else searchRequest.setGrouping(SearchGrouping.GROUP_NONE);

        return searchRequest;
    }

    private SearchResult handleResult(Long limit, SearchFilter searchFilter, SearchResponse searchResponse) {
        Map<String, Response> groups = searchResponse.getGrouped();
        Response response = searchResponse.getResponse();
        if (groups != null) return getGroupedSearchResult(limit, searchFilter, groups);
        if (response != null) return getSearchResult(limit, searchFilter, response);
        return new SearchResult();
    }

    private SearchResult getGroupedSearchResult(Long limit, SearchFilter searchFilter, Map<String, Response> groups) {
        SearchResult searchResult = new SearchResult();
        Map<String, SearchResult> resultGroups = new HashMap<>();
        for (Map.Entry<String, Response> group : groups.entrySet()) {
            String groupName = group.getKey().split(":")[0];
            SearchResult groupResult = getSearchResult(limit, searchFilter, group.getValue().getGroupResponse());
            resultGroups.put(groupName, groupResult);
        }

        searchResult.setGroups(resultGroups);
        return searchResult;
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
        List<Long> learningObjectIds = documents.stream().map(Document::getId).collect(Collectors.toList());
        List<Searchable> unsortedSearchable = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(learningObjectIds)) {
            reducedLearningObjectDao.findAllById(learningObjectIds).forEach(searchable -> {
                if (loggedInUser != null) {
                    UserFavorite userFavorite = userFavoriteDao.
                            findFavoriteByUserAndLearningObject(searchable.getId(), loggedInUser);
                    searchable.setFavorite(userFavorite != null);
                }
                unsortedSearchable.add(searchable);
            });
        }
        return unsortedSearchable;
    }


    private List<Searchable> sortSearchable(List<Document> indexList, List<Searchable> unsortedSearchable) {
        Map<Long, Searchable> idToSearchable = new HashMap<>();

        for (Searchable searchable : unsortedSearchable)
            idToSearchable.put(searchable.getId(), searchable);

        return indexList.stream()
                .filter(document -> idToSearchable.containsKey(document.getId()))
                .map(document -> idToSearchable.get(document.getId()))
                .collect(Collectors.toList());
    }

}
