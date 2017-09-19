package ee.hm.dop.config.guice.provider;

import static org.apache.commons.lang3.ArrayUtils.contains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;
import ee.hm.dop.service.solr.SolrEngineService;

/**
 * Guice provider of Search Engine Service.
 */
@Singleton
public class SearchEngineServiceTestProvider implements Provider<SolrEngineService> {

    @Override
    public synchronized SolrEngineService get() {
        return new SolrEngineServiceMock();
    }
}

class SolrEngineServiceMock implements SolrEngineService {

    private static final Map<String, List<Document>> searchResponses;

    private static final Table<String, String, List<Document>> sortedSearchResponses = HashBasedTable.create();

    private static final Long[] portfolioIds = {101L, 102L, 103L, 104L};

    private static final int RESULTS_PER_PAGE = 3;

    static {
        searchResponses = new HashMap<>();

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
        addQueryWithVisibility();
        addAdminQuery();

        addSortedQuery();
        addResourceTypeQuery();
        addEmptySearchQuery();
    }

    private static void addArabicQuery() {
        String arabicQuery = "((المدرسية) OR (\"المدرسية\")) AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> arabicSearchResult = createDocumentsWithIdentifiers(1L);
        searchResponses.put(arabicQuery, arabicSearchResult);
    }

    private static void addBigQuery() {
        String bigQuery = "((thishasmanyresults) OR (\"thishasmanyresults\")) AND ((visibility:\"public\") OR type:\"material\")";
        ArrayList<Document> bigQueryDocuments = new ArrayList<>();
        for (long i = 0; i < 8; i++) {
            addNewDocument(bigQueryDocuments, i);
        }

        searchResponses.put(bigQuery, bigQueryDocuments);
    }

    private static void addEmptyQueryWithTaxonEducationalContextFilter() {
        String filteredQuery = "educational_context:\"preschooleducation\""
                + " AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithEducationalContextFilter() {
        String filteredQuery = "((beethoven) OR (\"beethoven\")) AND domain:\"mathematics\" AND educational_context:\"preschooleducation\""
                + " AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 2L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithPaidFilterTrue() {
        String filteredQuery = "((dop) OR (\"dop\")) AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 3L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithPaidFilterFalse() {
        String filteredQuery = "((dop) OR (\"dop\")) AND (paid:\"false\" OR type:\"portfolio\")"
                + " AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 4L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithTypeFilter() {
        String filteredQuery = "((weird) OR (\"weird\")) AND type:\"portfolio\" AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 2L, 3L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithTypeFilterAll() {
        String filteredQuery = "((weird) OR (\"weird\")) AND (type:\"material\" OR type:\"portfolio\")"
                + " AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 5L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithTaxonSubjectAndPaidFilterFalse() {
        String filteredQuery = "((dop) OR (\"dop\")) AND subject:\"biology\" AND domain:\"mathematics\""
                + " AND educational_context:\"preschooleducation\" AND (paid:\"false\" OR type:\"portfolio\")"
                + " AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 6L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithTaxonSubjectAndTypeFilter() {
        String filteredQuery = "((beethoven) OR (\"beethoven\")) AND subject:\"mathematics\" AND domain:\"mathematics\""
                + " AND educational_context:\"preschooleducation\" AND type:\"material\""
                + " AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 7L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithPaidFalseAndTypeFilter() {
        String filteredQuery = "((weird) OR (\"weird\")) AND (paid:\"false\" OR type:\"portfolio\") AND type:\"material\""
                + " AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 8L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithAllFilters() {
        String filteredQuery = "((john) OR (\"john\")) AND educational_context:\"basiceducation\""
                + " AND (paid:\"false\" OR type:\"portfolio\") AND type:\"portfolio\""
                + " AND (issue_date_year:[2011 TO *] OR (added:[2011-01-01T00:00:00Z TO *] AND type:\"portfolio\"))"
                + " AND peerReview:[* TO *] AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 3L, 4L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithLanguage() {
        String filteredQuery = "((monday) OR (\"monday\")) AND (language:\"eng\" OR type:\"portfolio\")"
                + " AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 1L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithCurriculumLiteratureTrue() {
        String filteredQuery = "((data) OR (\"data\")) AND (peerReview:[* TO *] OR curriculum_literature:\"true\")"
                + " AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 7L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithCurriculumLiteratureFalse() {
        String filteredQuery = "((data) OR (\"data\"))"
                + " AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 8L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithVisibility() {
        String query = "((visibility:\"public\") OR type:\"material\")";
        List<Document> result = createDocumentsWithIdentifiers(2L, 3L);
        searchResponses.put(query, result);
    }

    private static void addAdminQuery() {
        String query = "((super) OR (\"super\")) AND ((visibility:\"public\" OR visibility:\"not_listed\" OR visibility:\"private\") OR type:\"material\")";
        List<Document> result = createDocumentsWithIdentifiers(2L, 4L);
        searchResponses.put(query, result);
    }

    private static void addIssuedFromQuery() {
        String query = "((car) OR (\"car\")) AND (issue_date_year:[2011 TO *] OR (added:[2011-01-01T00:00:00Z TO *] AND type:\"portfolio\"))"
                + " AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> result = createDocumentsWithIdentifiers(2L, 5L);
        searchResponses.put(query, result);
    }

    private static void addSortedQuery() {
        String query = "((tuesday) OR (\"tuesday\")) AND ((visibility:\"public\") OR type:\"material\")";
        String sort = "somefield desc";
        List<Document> result = createDocumentsWithIdentifiers(2L, 6L);
        sortedSearchResponses.put(query, sort, result);
    }

    private static void addResourceTypeQuery() {
        String query = "((ditmas) OR (\"ditmas\")) AND resource_type:\"experiment1\" AND ((visibility:\"public\") OR type:\"material\")";
        List<Document> result = createDocumentsWithIdentifiers(1L, 6L);
        searchResponses.put(query, result);
    }

    private static void addEmptySearchQuery() {
        String query = "((visibility:\"public\" OR visibility:\"not_listed\" OR visibility:\"private\") OR type:\"material\")";
        List<Document> result = createDocumentsWithIdentifiers(3L, 4L);
        searchResponses.put(query, result);
    }


    @Override
    public SearchResponse search(String query, long start, String sort, long limit) {
        if (sort == null) {
            return searchWithoutSorting(query, start, limit);
        } else {
            return searchWithSorting(query, sort, start, limit);
        }
    }

    @Override
    public List<String> suggest(String query, boolean searchTags) {
        return null;
    }

    @Override
    public SearchResponse search(String query, long start, String sort) {
        return search(query, start, sort, RESULTS_PER_PAGE);
    }

    private SearchResponse searchWithoutSorting(String query, long start, long limit) {
        if (!searchResponses.containsKey(query)) {
            return new SearchResponse();
        }

        List<Document> allDocuments = searchResponses.get(query);
        return getSearchResponse(start, limit, allDocuments);
    }

    private SearchResponse searchWithSorting(String query, String sort, long start, long limit) {
        if (!sortedSearchResponses.contains(query, sort)) {
            return new SearchResponse();
        }

        List<Document> allDocuments = sortedSearchResponses.get(query, sort);
        return getSearchResponse(start, limit, allDocuments);
    }

    private SearchResponse getSearchResponse(long start, long limit, List<Document> allDocuments) {
        List<Document> selectedDocuments = new ArrayList<>();
        for (int i = 0; i < allDocuments.size(); i++) {
            if (i >= start && i < start + RESULTS_PER_PAGE && selectedDocuments.size() < limit) {
                selectedDocuments.add(allDocuments.get(i));
            }
        }

        Response response = new Response();
        response.setDocuments(selectedDocuments);
        response.setStart(start);
        response.setTotalResults(allDocuments.size());

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResponse(response);

        return searchResponse;
    }

    @Override
    public void updateIndex() {

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

        if (contains(portfolioIds, id)) {
            newDocument.setType("portfolio");
        } else {
            newDocument.setType("material");
        }

        documents.add(newDocument);
    }
}
