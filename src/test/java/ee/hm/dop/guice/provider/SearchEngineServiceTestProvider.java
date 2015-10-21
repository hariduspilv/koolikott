package ee.hm.dop.guice.provider;

import static org.apache.commons.lang3.ArrayUtils.contains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final Long[] portfolioIds = { 1L, 2L, 3L };

    private static final int RESULTS_PER_PAGE = 3;

    static {
        searchResponses = new HashMap<>();

        addArabicQuery();
        addBigQuery();

        addEmptyQueryWithSubjectFilter();

        addQueryWithSubjectFilter();
        addQueryWithResourceTypeFilter();
        addQueryWithSubjectAndResourceTypeFilter();
        addQueryWithEducationalContextFilter();
        addQueryWithSubjectAndEducationalContextFilter();
        addQueryWithResourceTypeAndEducationalContextFilter();
        addQueryWithSubjectAndResourceTypeAndEducationalContextFilters();

        addQueryWithLicenseTypeFilter();
        addQueryWithSubjectAndLicenseTypeFilter();
        addQueryWithResourceTypeAndLicenseTypeFilter();
        addQueryWithSubjectAndResourceTypeAndLicenseTypeFilter();
        addQueryWithEducationalContextAndLicenseTypeFilter();
        addQueryWithSubjectAndEducationalContextAndLicenseTypeFilter();
        addQueryWithResourceTypeAndEducationalContextAndLicenseTypeFilter();
        addQueryWithAllFilters();

        addQueryWithTitleFilter();
        addQueryWithTitleAndSubjectFilter();
        addQueryWithTitleAndLicenseTypeAndResourceTypeFilter();

        addQueryWithAuthorFilter();
        addQueryWithesourceTypeAndAuthorFilter();
        addQueryWithEducationalContextAndTitleAndAuthorFilter();
    }

    private static void addArabicQuery() {
        String arabicQuery = "المدرسية*";
        List<Document> arabicSearchResult = createDocumentsWithIdentifiers(3L);
        searchResponses.put(arabicQuery, arabicSearchResult);
    }

    private static void addBigQuery() {
        String bigQuery = "thishasmanyresults*";
        ArrayList<Document> bigQueryDocuments = new ArrayList<>();
        for (long i = 0; i < 8; i++) {
            addNewDocument(bigQueryDocuments, i);
        }

        searchResponses.put(bigQuery, bigQueryDocuments);
    }

    private static void addEmptyQueryWithSubjectFilter() {
        String filteredQuery = "subject:\"interestingsubject\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(5L, 1L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    // Queries containing combinations of subject, resource and educational
    // context:

    private static void addQueryWithSubjectFilter() {
        String filteredQuery = "(filteredquery*) AND subject:\"mathematics\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(5L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithResourceTypeFilter() {
        String filteredQuery = "(beethoven*) AND resource_type:\"audio\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(4L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithSubjectAndResourceTypeFilter() {
        String filteredQuery = "(beethoven*) AND subject:\"mathematics\" AND resource_type:\"audio\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(7L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithEducationalContextFilter() {
        String filteredQuery = "(beethoven*) AND educational_context:\"preschool\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(6L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithSubjectAndEducationalContextFilter() {
        String filteredQuery = "(beethoven*) AND subject:\"mathematics\" AND educational_context:\"preschool\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(8L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithResourceTypeAndEducationalContextFilter() {
        String filteredQuery = "(beethoven*) AND resource_type:\"audio\" AND educational_context:\"preschool\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(7L, 8L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithSubjectAndResourceTypeAndEducationalContextFilters() {
        String filteredQuery = "(john*) AND subject:\"mathematics\" AND resource_type:\"audio\" AND educational_context:\"preschool\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    // Queries with license type

    private static void addQueryWithLicenseTypeFilter() {
        String filteredQuery = "(database*) AND license_type:\"cc\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 1L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithSubjectAndLicenseTypeFilter() {
        String filteredQuery = "(filteredquery*) AND subject:\"mathematics\" AND license_type:\"ccby\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 1L, 3L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithResourceTypeAndLicenseTypeFilter() {
        String filteredQuery = "(beethoven*) AND resource_type:\"audio\" AND license_type:\"ccbysa\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 3L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithSubjectAndResourceTypeAndLicenseTypeFilter() {
        String filteredQuery = "(beethoven*) AND subject:\"mathematics\" AND resource_type:\"audio\" AND license_type:\"ccbynd\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 4L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithEducationalContextAndLicenseTypeFilter() {
        String filteredQuery = "(beethoven*) AND educational_context:\"preschool\" AND license_type:\"ccbysa\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 5L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithSubjectAndEducationalContextAndLicenseTypeFilter() {
        String filteredQuery = "(beethoven*) AND subject:\"mathematics\" AND educational_context:\"preschool\" AND license_type:\"ccbync\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 6L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithResourceTypeAndEducationalContextAndLicenseTypeFilter() {
        String filteredQuery = "(beethoven*) AND resource_type:\"audio\" AND educational_context:\"preschool\" AND license_type:\"ccbynd\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 7L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithAllFilters() {
        String filteredQuery = "(john*) AND subject:\"mathematics\" AND resource_type:\"audio\" AND educational_context:\"preschool\""
                + " AND license_type:\"other\" AND title:\"smith\" AND author:\"mary\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 8L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    // Queries with title (only some combinations)

    private static void addQueryWithTitleFilter() {
        String filteredQuery = "(web) AND title:\"www\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(3L, 1L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithTitleAndSubjectFilter() {
        String filteredQuery = "(web) AND subject:\"algebra\" AND title:\"www\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(3L, 2L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithTitleAndLicenseTypeAndResourceTypeFilter() {
        String filteredQuery = "(web) AND resource_type:\"video\" AND license_type:\"other\" AND title:\"www\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(3L, 4L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    // Queries with author

    private static void addQueryWithAuthorFilter() {
        String filteredQuery = "(books*) AND author:\"mary\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(4L, 1L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithesourceTypeAndAuthorFilter() {
        String filteredQuery = "(books*) AND resource_type:\"unknown\" AND author:\"mary\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(4L, 2L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithEducationalContextAndTitleAndAuthorFilter() {
        String filteredQuery = "(other* books*) AND educational_context:\"teachereducation\""
                + " AND title:\"cool\\ title\" AND author:\"mary\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(4L, 3L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    @Override
    public SearchResponse search(String query, long start) {
        if (!searchResponses.containsKey(query)) {
            return new SearchResponse();
        }

        List<Document> allDocuments = searchResponses.get(query);
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
