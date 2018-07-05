package ee.hm.dop.config.guice.provider;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SolrSearchResponse;
import ee.hm.dop.service.SuggestionStrategy;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.solr.SolrSearchRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.ArrayUtils.contains;

/**
 * Guice provider of Search Engine Service.
 */
@Singleton
public class SearchEngineServiceTestProvider implements Provider<SolrEngineService> {

    private SolrEngineServiceMock instance = new SolrEngineServiceMock();

    @Override
    public synchronized SolrEngineService get() {
        return instance;
    }
}

class SolrEngineServiceMock implements SolrEngineService {

    private static final Map<String, List<Document>> searchResponses;
    private static final Map<String, List<Document>> groupedSearchResponses;

    private static final Table<String, String, List<Document>> sortedSearchResponses = HashBasedTable.create();

    private static final Long[] portfolioIds = {TestConstants.PORTFOLIO_1, TestConstants.PORTFOLIO_2, TestConstants.PORTFOLIO_3, TestConstants.PORTFOLIO_4};

    private static final int RESULTS_PER_PAGE = 3;
    private static final int RESULTS_LIMIT = 24;

    static {
        searchResponses = new HashMap<>();
        groupedSearchResponses = new HashMap<>();

        addArabicQuery();
        addBigQuery();

        addEmptyQueryWithTaxonEducationalContextFilter();

        addQueryWithEducationalContextFilter();
        addQueryWithPaidFilterTrue();
        addQueryWithPaidFilterFalse();
        addQueryWithTypeFilter();
        addQueryWithTypeFilterAll();
        addQueryWithTaxonSubjectAndPaidFilterFalse();
        addQueryWithTaxonSubjectAndTypeFilter();
        addQueryWithPaidFalseAndTypeFilter();
        addIssuedFromQuery();
        addQueryWithAllFilters();

        addQueryWithLanguage();
        addQueryWithCurriculumLiteratureTrue();
        addQueryWithCurriculumLiteratureFalse();
        addQueryWithRecommendedTrue();
        addQueryWithRecommendedTrueFavoredTruePeeter();
        addQueryWithFavoredTruePeeter();
        addQueryWithVisibility();
        addAdminQuery();

        addSortedQuery();
        addResourceTypeQuery();
        addEmptySearchQuery();
    }

    private static void addQueryWithRecommendedTrueFavoredTruePeeter() {
        String filteredQuery = "((data) OR (\"data\")) AND (recommended:\"true\" OR favored_by_user:\"peeter.paan\") AND (visibility:\"public\")";
        List<Document> result = createDocumentsWithIdentifiers(1L, 7L);
        searchResponses.put(filteredQuery, result);
    }

    private static void addQueryWithFavoredTruePeeter() {
        String filteredQuery = "((data) OR (\"data\")) AND favored_by_user:\"peeter.paan\" AND (visibility:\"public\")";
        List<Document> result = createDocumentsWithIdentifiers(1L, 7L);
        searchResponses.put(filteredQuery, result);
    }

    private static void addQueryWithRecommendedTrue() {
        String filteredQuery = "((data) OR (\"data\")) AND recommended:\"true\" AND (visibility:\"public\")";
        List<Document> result = createDocumentsWithIdentifiers(1L, 7L);
        searchResponses.put(filteredQuery, result);
    }

    private static void addArabicQuery() {
        String arabicQuery = "((المدرسية) OR (\"المدرسية\")) AND (visibility:\"public\")";
        List<Document> arabicSearchResult = createDocumentsWithIdentifiers(1L);
        searchResponses.put(arabicQuery, arabicSearchResult);
    }

    private static void addBigQuery() {
        String bigQuery = "((thishasmanyresults) OR (\"thishasmanyresults\")) AND (visibility:\"public\")";
        ArrayList<Document> bigQueryDocuments = new ArrayList<>();
        for (long i = 0; i < 8; i++) {
            addNewDocument(bigQueryDocuments, i);
        }

        searchResponses.put(bigQuery, bigQueryDocuments);
    }

    private static void addEmptyQueryWithTaxonEducationalContextFilter() {
        String filteredQuery = "educational_context:\"preschooleducation\""
                + " AND (visibility:\"public\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithEducationalContextFilter() {
        String filteredQuery = "((beethoven) OR (\"beethoven\")) AND domain:\"mathematics\" AND educational_context:\"preschooleducation\""
                + " AND (visibility:\"public\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 2L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithPaidFilterTrue() {
        String filteredQuery = "((dop) OR (\"dop\")) AND (visibility:\"public\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 3L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithPaidFilterFalse() {
        String filteredQuery = "((dop) OR (\"dop\")) AND (paid:\"false\" OR type:\"portfolio\")"
                + " AND (visibility:\"public\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 4L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithTypeFilter() {
        String filteredQuery = "((weird) OR (\"weird\")) AND type:\"portfolio\" AND (visibility:\"public\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 2L, 3L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithTypeFilterAll() {
        String filteredQuery = "((weird) OR (\"weird\")) AND (type:\"material\" OR type:\"portfolio\")"
                + " AND (visibility:\"public\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 5L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithTaxonSubjectAndPaidFilterFalse() {
        String filteredQuery = "((dop) OR (\"dop\")) AND subject:\"biology\" AND domain:\"mathematics\""
                + " AND educational_context:\"preschooleducation\" AND (paid:\"false\" OR type:\"portfolio\")"
                + " AND (visibility:\"public\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 6L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithTaxonSubjectAndTypeFilter() {
        String filteredQuery = "((beethoven) OR (\"beethoven\")) AND subject:\"mathematics\" AND domain:\"mathematics\""
                + " AND educational_context:\"preschooleducation\" AND type:\"material\""
                + " AND (visibility:\"public\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 7L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithPaidFalseAndTypeFilter() {
        String filteredQuery = "((weird) OR (\"weird\")) AND (paid:\"false\" OR type:\"portfolio\") AND type:\"material\""
                + " AND (visibility:\"public\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 8L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithAllFilters() {
        String filteredQuery = "((john) OR (\"john\")) AND educational_context:\"basiceducation\""
                + " AND (paid:\"false\" OR type:\"portfolio\") AND type:\"portfolio\""
                + " AND (issue_date_year:[2011 TO *] OR (added:[2011-01-01T00:00:00Z TO *] AND type:\"portfolio\"))"
                + " AND peerReview:[* TO *] AND (visibility:\"public\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 3L, 4L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithLanguage() {
        String filteredQuery = "((monday) OR (\"monday\")) AND (language:\"eng\" OR type:\"portfolio\")"
                + " AND (visibility:\"public\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 1L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithCurriculumLiteratureTrue() {
        String filteredQuery = "((data) OR (\"data\")) AND (peerReview:[* TO *] OR curriculum_literature:\"true\")"
                + " AND (visibility:\"public\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 7L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithCurriculumLiteratureFalse() {
        String filteredQuery = "((data) OR (\"data\"))"
                + " AND (visibility:\"public\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 8L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithVisibility() {
        String query = "(visibility:\"public\")";
        List<Document> result = createDocumentsWithIdentifiers(2L, 3L);
        searchResponses.put(query, result);
    }

    private static void addAdminQuery() {
        String query = "((super) OR (\"super\")) AND (visibility:\"public\" OR visibility:\"not_listed\" OR visibility:\"private\")";
        List<Document> result = createDocumentsWithIdentifiers(2L, 4L);
        searchResponses.put(query, result);
    }

    private static void addIssuedFromQuery() {
        String query = "((car) OR (\"car\")) AND (issue_date_year:[2011 TO *] OR (added:[2011-01-01T00:00:00Z TO *] AND type:\"portfolio\"))"
                + " AND (visibility:\"public\")";
        List<Document> result = createDocumentsWithIdentifiers(2L, 5L);
        searchResponses.put(query, result);
    }

    private static void addSortedQuery() {
        String query = "((tuesday) OR (\"tuesday\")) AND (visibility:\"public\")";
        String sort = "type desc, added desc, visibility asc, id desc";
        List<Document> result = createDocumentsWithIdentifiers(2L, 6L);
        sortedSearchResponses.put(query, sort, result);
    }

    private static void addResourceTypeQuery() {
        String query = "((ditmas) OR (\"ditmas\")) AND resource_type:\"experiment1\" AND (visibility:\"public\")";
        List<Document> result = createDocumentsWithIdentifiers(1L, 6L);
        searchResponses.put(query, result);
    }

    private static void addEmptySearchQuery() {
        String query = "((visibility:\"public\" OR visibility:\"not_listed\" OR visibility:\"private\"))";
        List<Document> result = createDocumentsWithIdentifiers(3L, 4L);
        searchResponses.put(query, result);
    }

    private static void addGroupedSearchQueryKaru() {
        String query = "((karu) OR (\"karu\")) AND (type:\"material\" OR type:\"portfolio\") AND (visibility:\"public\" OR visibility:\"not_listed\" OR visibility:\"private\"";
        List<Document> result = createDocumentsWithIdentifiers(1L, 7L);
        groupedSearchResponses.put(query, result);
    }

    private static List<Document> createDocumentsWithIdentifiers(Long... identifiers) {
        List<Document> documents = new ArrayList<>();
        for (Long id : identifiers) {
            addNewDocument(documents, id);
        }

        return documents;
    }

    private static void addNewDocument(List<Document> documents, Long id) {
        Document newDocument = new Document();
        newDocument.setId(Long.toString(id));

        if (contains(portfolioIds, id)) newDocument.setType("portfolio");
        else newDocument.setType("material");

        documents.add(newDocument);
    }

    @Override
    public SolrSearchResponse search(SolrSearchRequest searchRequest) {
        if (searchRequest.getSort() == null) return searchWithoutSorting(searchRequest);
        else return searchWithSorting(searchRequest);
    }

    @Override
    public SolrSearchResponse limitlessSearch(SolrSearchRequest searchRequest) {
        return null;
    }

    @Override
    public List<String> suggest(String query, SuggestionStrategy suggestionStrategy) {
        return null;
    }

    private SolrSearchResponse searchWithoutSorting(SolrSearchRequest searchRequest) {
        String query = searchRequest.getSolrQuery();
        long start = searchRequest.getFirstItem();
        Long limit = searchRequest.getItemLimit();
        if (!searchResponses.containsKey(query)) return new SolrSearchResponse();
        List<Document> allDocuments = searchResponses.get(query);
        return getSearchResponse(start, limit, allDocuments);
    }

    private SolrSearchResponse searchWithSorting(SolrSearchRequest searchRequest) {
        String query = searchRequest.getSolrQuery();
        String sort = searchRequest.getSort();
        long start = searchRequest.getFirstItem();
        Long limit = searchRequest.getItemLimit();
        if (!sortedSearchResponses.contains(query, sort)) {
            if (searchResponses.containsKey(query)) {
                List<Document> allDocuments = searchResponses.get(query);
                return getSearchResponse(start, limit, allDocuments);
            } else return new SolrSearchResponse();
        }

        List<Document> allDocuments = sortedSearchResponses.get(query, sort);
        return getSearchResponse(start, limit, allDocuments);
    }

    private SolrSearchResponse getSearchResponse(long start, long limit, List<Document> allDocuments) {
        List<Document> selectedDocuments = new ArrayList<>();
        limit = limit == 0 ? RESULTS_LIMIT : limit;
        for (int i = (int) start; i < allDocuments.size(); i++) {
            if (i < start + RESULTS_PER_PAGE && selectedDocuments.size() < limit) {
                selectedDocuments.add(allDocuments.get(i));
            }
        }

        Response response = new Response();
        response.setDocuments(selectedDocuments);
        response.setStart(start);
        response.setTotalResults(allDocuments.size());

        SolrSearchResponse searchResponse = new SolrSearchResponse();
        searchResponse.setResponse(response);

        return searchResponse;
    }

    @Override
    public void updateIndex() {

    }
}
