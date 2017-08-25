package ee.hm.dop.service.solr;

import ee.hm.dop.dao.ReducedLearningObjectDAO;
import ee.hm.dop.dao.UserFavoriteDAO;
import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserFavorite;
import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ee.hm.dop.service.solr.SolrService.getTokenizedQueryString;
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
    private UserFavoriteDAO userFavoriteDAO;
    @Inject
    private ReducedLearningObjectDAO reducedLearningObjectDAO;

    public SearchResult search(String query, long start, Long limit, SearchFilter searchFilter) {
        SearchResult searchResult = new SearchResult();

        searchFilter.setVisibility(SearchConverter.getSearchVisibility(searchFilter.getRequestingUser()));
        Long resultCount;
        if (limit != null && limit == 0) resultCount = null;
        else resultCount = limit;

        SearchResponse searchResponse = doSearch(query, start, resultCount, searchFilter);
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
        if (!learningObjectIds.isEmpty()) {
            reducedLearningObjectDAO.findAllById(learningObjectIds).forEach(searchable -> {
                if (loggedInUser != null) {
                    UserFavorite userFavorite = userFavoriteDAO.findFavoriteByUserAndLearningObject(searchable.getId(), loggedInUser);
                    searchable.setFavorite(userFavorite != null && userFavorite.getId() != null);
                }
                unsortedSearchable.add(searchable);
            });
        }
        return unsortedSearchable;
    }

    private SearchResponse doSearch(String query, long start, Long limit, SearchFilter searchFilter) {
        String tokenizedQueryString = getTokenizedQueryString(query);
        String queryString = EMPTY;

        String filtersAsQuery = getFiltersAsQuery(searchFilter);
        if (StringUtils.isNotEmpty(filtersAsQuery)) {
            if (StringUtils.isEmpty(tokenizedQueryString)) {
                queryString = filtersAsQuery;
            } else {
                queryString = format("((%s)", tokenizedQueryString);

                //Search for full phrase also, as they are more relevant
                if (fullPhraseSearch(tokenizedQueryString)) {
                    queryString = queryString.concat(format(" OR (\"%s\")", tokenizedQueryString));
                }

                queryString = queryString.concat(format(") %s %s", searchFilter.getSearchType(), filtersAsQuery));
            }
        }
        if (queryString.isEmpty()) {
            throw new RuntimeException("No query string and filters present.");
        }
        if (limit == null) {
            return solrEngineService.search(queryString, start, getSort(searchFilter));
        }
        return solrEngineService.search(queryString, start, limit, getSort(searchFilter));
    }

    private boolean fullPhraseSearch(String tokenizedQueryString) {
        return !tokenizedQueryString.toLowerCase().startsWith(SEARCH_BY_TAG_PREFIX)
                && !tokenizedQueryString.toLowerCase().startsWith(SEARCH_RECOMMENDED_PREFIX)
                && !tokenizedQueryString.toLowerCase().startsWith(SEARCH_BY_AUTHOR_PREFIX);
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

    /*
     * Convert filters to Solr syntax query
     */
    private String getFiltersAsQuery(SearchFilter searchFilter) {
        List<String> filters = new LinkedList<>();

        filters.add(SearchConverter.getLanguageAsQuery(searchFilter));
        filters.add(SearchConverter.getTaxonsAsQuery(searchFilter));
        filters.add(SearchConverter.isPaidAsQuery(searchFilter));
        filters.add(SearchConverter.getTypeAsQuery(searchFilter));
        filters.add(SearchConverter.getTargetGroupsAsQuery(searchFilter));
        filters.add(SearchConverter.getResourceTypeAsQuery(searchFilter));
        filters.add(SearchConverter.isSpecialEducationAsQuery(searchFilter));
        filters.add(SearchConverter.issuedFromAsQuery(searchFilter));
        filters.add(SearchConverter.getCrossCurricularThemesAsQuery(searchFilter));
        filters.add(SearchConverter.getKeyCompetencesAsQuery(searchFilter));
        filters.add(SearchConverter.isCurriculumLiteratureAsQuery(searchFilter));
        filters.add(SearchConverter.getVisibilityAsQuery(searchFilter));
        filters.add(SearchConverter.getCreatorAsQuery(searchFilter));

        // Remove empty elements
        filters = filters.stream().filter(f -> !f.isEmpty()).collect(Collectors.toList());
        String query = StringUtils.join(filters, format(" %s ", searchFilter.getSearchType()));
        return query.concat(SearchConverter.getExcludedAsQuery(searchFilter));
    }
}
