package ee.hm.dop.guice.provider;

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
import ee.hm.dop.service.SearchEngineService;

/**
 * Guice provider of Search Engine Service.
 */
@Singleton
public class SearchEngineServiceTestProvider implements Provider<SearchEngineService> {

    @Override
    public synchronized SearchEngineService get() {
        return new SearchEngineServiceMock();
    }
}

class SearchEngineServiceMock implements SearchEngineService {

    private static final Map<String, List<Document>> searchResponses;

    private static final Table<String, String, List<Document>> sortedSearchResponses = HashBasedTable.create();

    private static final Long[] portfolioIds = { 1L, 2L, 3L, 4L };

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
        addQueryWithVisibility();
        addAdminQuery();

        addSortedQuery();
    }

    private static void addArabicQuery() {
        String arabicQuery = "(المدرسية*) AND (visibility:\"public\" OR type:\"material\")";
        List<Document> arabicSearchResult = createDocumentsWithIdentifiers(1L);
        searchResponses.put(arabicQuery, arabicSearchResult);
    }

    private static void addBigQuery() {
        String bigQuery = "(thishasmanyresults*) AND (visibility:\"public\" OR type:\"material\")";
        ArrayList<Document> bigQueryDocuments = new ArrayList<>();
        for (long i = 0; i < 8; i++) {
            addNewDocument(bigQueryDocuments, i);
        }

        searchResponses.put(bigQuery, bigQueryDocuments);
    }

    private static void addEmptyQueryWithTaxonEducationalContextFilter() {
        String filteredQuery = "educational_context:\"preschooleducation\""
                + " AND (visibility:\"public\" OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithEducationalContextFilter() {
        String filteredQuery = "(beethoven*) AND domain:\"mathematics\" AND educational_context:\"preschooleducation\""
                + " AND (visibility:\"public\" OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 2L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithPaidFilterTrue() {
        String filteredQuery = "(dop) AND (visibility:\"public\" OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 3L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithPaidFilterFalse() {
        String filteredQuery = "(dop) AND (paid:\"false\" OR type:\"portfolio\")"
                + " AND (visibility:\"public\" OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 4L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithTypeFilter() {
        String filteredQuery = "(weird*) AND type:\"portfolio\" AND (visibility:\"public\" OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 2L, 3L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithTypeFilterAll() {
        String filteredQuery = "(weird*) AND (type:\"material\" OR type:\"portfolio\")"
                + " AND (visibility:\"public\" OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 5L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithTaxonSubjectAndPaidFilterFalse() {
        String filteredQuery = "(dop) AND subject:\"biology\" AND domain:\"mathematics\""
                + " AND educational_context:\"preschooleducation\" AND (paid:\"false\" OR type:\"portfolio\")"
                + " AND (visibility:\"public\" OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 6L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithTaxonSubjectAndTypeFilter() {
        String filteredQuery = "(beethoven*) AND subject:\"mathematics\" AND domain:\"mathematics\""
                + " AND educational_context:\"preschooleducation\" AND type:\"material\""
                + " AND (visibility:\"public\" OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 7L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithPaidFalseAndTypeFilter() {
        String filteredQuery = "(weird*) AND (paid:\"false\" OR type:\"portfolio\") AND type:\"material\""
                + " AND (visibility:\"public\" OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 8L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithAllFilters() {
        String filteredQuery = "(john*) AND educational_context:\"basiceducation\""
                + " AND (paid:\"false\" OR type:\"portfolio\") AND type:\"portfolio\""
                + " AND (issue_date_year:[2011 TO *] OR (created:[2011-01-01T00:00:00Z TO *] AND type:\"portfolio\"))"
                + " AND (visibility:\"public\" OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 3L, 4L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithLanguage() {
        String filteredQuery = "(monday*) AND (language:\"eng\" OR type:\"portfolio\")"
                + " AND (visibility:\"public\" OR type:\"material\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 1L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithVisibility() {
        String query = "(visibility:\"public\" OR type:\"material\")";
        List<Document> result = createDocumentsWithIdentifiers(2L, 3L);
        searchResponses.put(query, result);
    }

    private static void addAdminQuery() {
        String query = "super*";
        List<Document> result = createDocumentsWithIdentifiers(2L, 4L);
        searchResponses.put(query, result);
    }

    private static void addIssuedFromQuery() {
        String query = "(car) AND (issue_date_year:[2011 TO *] OR (created:[2011-01-01T00:00:00Z TO *] AND type:\"portfolio\"))"
                + " AND (visibility:\"public\" OR type:\"material\")";
        List<Document> result = createDocumentsWithIdentifiers(2L, 5L);
        searchResponses.put(query, result);
    }

    private static void addSortedQuery() {
        String query = "(tuesday*) AND (visibility:\"public\" OR type:\"material\")";
        String sort = "somefield desc";
        List<Document> result = createDocumentsWithIdentifiers(2L, 6L);
        sortedSearchResponses.put(query, sort, result);
    }

    @Override
    public SearchResponse search(String query, long start, String sort) {
        if (sort == null) {
            return searchWithoutSorting(query, start);
        } else {
            return searchWithSorting(query, sort, start);
        }
    }

    private SearchResponse searchWithoutSorting(String query, long start) {
        if (!searchResponses.containsKey(query)) {
            return new SearchResponse();
        }

        List<Document> allDocuments = searchResponses.get(query);
        return getSearchResponse(start, allDocuments);
    }

    private SearchResponse searchWithSorting(String query, String sort, long start) {
        if (!sortedSearchResponses.contains(query, sort)) {
            return new SearchResponse();
        }

        List<Document> allDocuments = sortedSearchResponses.get(query, sort);
        return getSearchResponse(start, allDocuments);
    }

    private SearchResponse getSearchResponse(long start, List<Document> allDocuments) {
        List<Document> selectedDocuments = new ArrayList<>();
        for (int i = 0; i < allDocuments.size(); i++) {
            if (i >= start && i < start + RESULTS_PER_PAGE) {
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
