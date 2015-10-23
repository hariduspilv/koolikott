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

    private static final Long[] portfolioIds = { 1L, 2L, 3L, 4L };

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

        addQueryWithCombinedDescriptionFilter();
        addQueryWithLicenseTypeAndCombinedDescriptionFilter();
        addQueryWithEducationalContextAndCombinedDescriptionFilter();

        addQueryWithPaidFilterTrue();
        addQueryWithPaidFilterFalse();
        addQueryWithEducationalContextAndPaidFilterFalse();
        addQueryWithSubjectAndTitleAndAndPaidFilterFalse();

        addQueryWithTypeFilter();
        addQueryWithCombinedDescriptionAndTypeFilter();
        addQueryWithPaidFalseAndTypeFilter();
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
                + " AND license_type:\"other\" AND (description:\"desc\" OR summary:\"desc\")"
                + " AND (paid:\"false\" OR type:\"portfolio\") AND type:\"portfolio\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(2L, 3L, 4L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    // Queries with combined description

    private static void addQueryWithCombinedDescriptionFilter() {
        String filteredQuery = "(material*) AND (description:\"the\\ description.\" OR summary:\"the\\ description.\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(5L, 1L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithLicenseTypeAndCombinedDescriptionFilter() {
        String filteredQuery = "(material*) AND license_type:\"ccbyncnd\""
                + " AND (description:\"another\\ description.\" OR summary:\"another\\ description.\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(5L, 2L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithEducationalContextAndCombinedDescriptionFilter() {
        String filteredQuery = "(material*) AND educational_context:\"highereducation\""
                + " AND (description:\"more\\ descriptions.\" OR summary:\"more\\ descriptions.\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(5L, 3L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    // Queries with no paid materials

    private static void addQueryWithPaidFilterTrue() {
        String filteredQuery = "dop";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(6L, 4L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithPaidFilterFalse() {
        String filteredQuery = "(dop) AND (paid:\"false\" OR type:\"portfolio\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(6L, 1L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithEducationalContextAndPaidFilterFalse() {
        String filteredQuery = "(dop) AND educational_context:\"specialeducation\""
                + " AND (paid:\"false\" OR type:\"portfolio\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(6L, 2L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithSubjectAndTitleAndAndPaidFilterFalse() {
        String filteredQuery = "(dop) AND subject:\"music\" AND (paid:\"false\" OR type:\"portfolio\")";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(6L, 3L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    // Queries with type

    private static void addQueryWithTypeFilter() {
        String filteredQuery = "(weird*) AND type:\"portfolio\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(1L, 2L, 3L, 4L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithCombinedDescriptionAndTypeFilter() {
        String filteredQuery = "(weird*) AND (description:\"aliens\" OR summary:\"aliens\") AND type:\"material\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(7L, 1L);
        searchResponses.put(filteredQuery, filteredSearchResult);
    }

    private static void addQueryWithPaidFalseAndTypeFilter() {
        String filteredQuery = "(weird*) AND (paid:\"false\" OR type:\"portfolio\")" + " AND type:\"material\"";
        List<Document> filteredSearchResult = createDocumentsWithIdentifiers(7L, 2L);
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
