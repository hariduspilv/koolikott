package ee.hm.dop.service.solr;

import ee.hm.dop.dao.LearningObjectDAO;
import ee.hm.dop.dao.ReducedLearningObjectDAO;
import ee.hm.dop.dao.UserFavoriteDao;
import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserFavorite;
import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class SearchService {

    public static final String MATERIAL_TYPE = "material";
    public static final String PORTFOLIO_TYPE = "portfolio";
    public static final String ALL_TYPE = "all";
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
    private ReducedLearningObjectDAO reducedLearningObjectDAO;
    @Inject
    private LearningObjectDAO learningObjectDAO;

    public SearchResult search(String query, long start, Long limit, SearchFilter searchFilter) {
        searchFilter.setVisibility(SearchConverter.getSearchVisibility(searchFilter.getRequestingUser()));
        String queryString = SearchConverter.composeQueryString(query, searchFilter);

        SearchResponse searchResponse = search(start, limit, searchFilter, queryString);

//        if (StringUtils.isBlank(query) && searchFilter.isEmpty()){
//            Long count = learningObjectDAO.findAllNotDeleted();
//            searchResponse.getResponse().setTotalResults(count);
//        }

        return handleResult(limit, searchFilter, searchResponse);
    }

    private SearchResponse search(long start, Long limit, SearchFilter searchFilter, String queryString) {
        if (limit == null || limit == 0) {
            return solrEngineService.search(queryString, start, getSort(searchFilter));
        }
        return solrEngineService.search(queryString, start, limit, getSort(searchFilter));
    }

    private SearchResult handleResult(Long limit, SearchFilter searchFilter, SearchResponse searchResponse) {
        SearchResult searchResult = new SearchResult();
        Response response = searchResponse.getResponse();
        if (response != null) {
            List<Document> documents = response.getDocuments();
            List<Searchable> unsortedSearchable = retrieveSearchedItems(documents, searchFilter.getRequestingUser());
            List<Searchable> sortedSearchable = sortSearchable(documents, unsortedSearchable);

            searchResult.setStart(response.getStart());
            searchResult.setTotalResults(response.getTotalResults() - documents.size() + sortedSearchable.size());

            if (limit == null || limit > 0) {
                searchResult.setItems(sortedSearchable);
            }
        }
        return searchResult;
    }

    private List<Searchable> retrieveSearchedItems(List<Document> documents, User loggedInUser) {
        List<Long> learningObjectIds = documents.stream().map(Document::getId).collect(Collectors.toList());
        List<Searchable> unsortedSearchable = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(learningObjectIds)) {
            reducedLearningObjectDAO.findAllById(learningObjectIds).forEach(searchable -> {
                if (loggedInUser != null) {
                    UserFavorite userFavorite = userFavoriteDao.findFavoriteByUserAndLearningObject(searchable.getId(), loggedInUser);
                    searchable.setFavorite(userFavorite != null && userFavorite.getId() != null);
                }
                unsortedSearchable.add(searchable);
            });
        }
        return unsortedSearchable;
    }

    private String getSort(SearchFilter searchFilter) {
        if (searchFilter.getSort() != null && searchFilter.getSortDirection() != null) {
            return String.join(" ", searchFilter.getSort(), searchFilter.getSortDirection().getValue());
        }
        return null;
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
