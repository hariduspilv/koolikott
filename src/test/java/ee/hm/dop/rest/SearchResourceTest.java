package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.SearchResult;

public class SearchResourceTest extends ResourceIntegrationTestBase {

    private static final int RESULTS_PER_PAGE = 3;

    @Test
    public void search() {
        String query = "المدرسية";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, null, null, null), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 3L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchGetSecondPage() {
        String query = "thishasmanyresults";
        int start = RESULTS_PER_PAGE;
        SearchResult searchResult = doGet(buildQueryURL(query, start, null, null, null, null), SearchResult.class);

        assertEquals(RESULTS_PER_PAGE, searchResult.getMaterials().size());
        for (int i = 0; i < RESULTS_PER_PAGE; i++) {
            assertEquals(Long.valueOf(i + start), searchResult.getMaterials().get(i).getId());
        }
        assertEquals(8, searchResult.getTotalResults());
        assertEquals(start, searchResult.getStart());

    }

    @Test
    public void searchNoResult() {
        String query = "no+results";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, null, null, null), SearchResult.class);

        assertEquals(0, searchResult.getMaterials().size());
    }

    @Test
    public void searchWithSubjectFilter() {
        String query = "filteredquery";
        String subject = "Mathematics";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, subject, null, null, null), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 5L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithResourceTypeFilter() {
        String query = "beethoven";
        String resourceType = "Audio";
        String queryURL = buildQueryURL(query, 0, null, resourceType, null, null);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 4L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithSubjectAndResourceTypeFilter() {
        String query = "beethoven";
        String subject = "Mathematics";
        String resourceType = "Audio";
        String queryURL = buildQueryURL(query, 0, subject, resourceType, null, null);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 7L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithEducationalContextFilter() {
        String query = "beethoven";
        String educationalContext = "preschool";
        String queryURL = buildQueryURL(query, 0, null, null, educationalContext, null);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 6L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithSubjectAndEducationalContextFilter() {
        String query = "beethoven";
        String subject = "Mathematics";
        String educationalContext = "Preschool";
        String queryURL = buildQueryURL(query, 0, subject, null, educationalContext, null);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 8L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithResourceTypeAndEducationalContextFilter() {
        String query = "beethoven";
        String resourceType = "audio";
        String educationalContext = "preschool";
        String queryURL = buildQueryURL(query, 0, null, resourceType, educationalContext, null);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 7L, 8L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithSubjectAndResourceTypeAndEducationalContextFilters() {
        String query = "john";
        String subject = "Mathematics";
        String resourceType = "Audio";
        String educationalContext = "Preschool";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, subject, resourceType, educationalContext, null),
                SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 2L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    // Tests with License type

    @Test
    public void searchWithLicenseType() {
        String query = "database";
        String licenseType = "CC";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, null, null, licenseType), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 2L, 1L);
        assertEquals(0L, searchResult.getStart());
        assertEquals(2L, searchResult.getTotalResults());
    }

    @Test
    public void searchWithSubjectAndLicenseTypeFilter() {
        String query = "filteredquery";
        String subject = "Mathematics";
        String licenseType = "CCBY";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, subject, null, null, licenseType),
                SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 2L, 1L, 3L);
        assertEquals(3, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithResourceTypeAndLicenseTypeFilter() {
        String query = "beethoven";
        String resourceType = "Audio";
        String licenseType = "CCBYSA";
        String queryURL = buildQueryURL(query, 0, null, resourceType, null, licenseType);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 2L, 3L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithSubjectAndResourceTypeAndLicenseTypeFilter() {
        String query = "beethoven";
        String subject = "Mathematics";
        String resourceType = "Audio";
        String licenseType = "CCBYND";
        String queryURL = buildQueryURL(query, 0, subject, resourceType, null, licenseType);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 2L, 4L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithEducationalContextAndLicenseTypeFilter() {
        String query = "beethoven";
        String educationalContext = "preschool";
        String licenseType = "CCBYSA";
        String queryURL = buildQueryURL(query, 0, null, null, educationalContext, licenseType);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 2L, 5L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithSubjectAndEducationalContextAndLicenseTypeFilter() {
        String query = "beethoven";
        String subject = "Mathematics";
        String educationalContext = "Preschool";
        String licenseType = "CCBYNC";
        String queryURL = buildQueryURL(query, 0, subject, null, educationalContext, licenseType);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 2L, 6L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithResourceTypeAndEducationalContextAndLicenseTypeFilter() {
        String query = "beethoven";
        String resourceType = "audio";
        String educationalContext = "preschool";
        String licenseType = "CCBYND";
        String queryURL = buildQueryURL(query, 0, null, resourceType, educationalContext, licenseType);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 2L, 7L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithAllFilters() {
        String query = "john";
        String subject = "Mathematics";
        String resourceType = "Audio";
        String educationalContext = "Preschool";
        String licenseType = "other";
        SearchResult searchResult = doGet(
                buildQueryURL(query, 0, subject, resourceType, educationalContext, licenseType), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getMaterials(), 2L, 8L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    private String buildQueryURL(String query, int start, String subject, String resourceType,
            String educationalContext, String licenseType) {
        String queryURL = "search?";
        if (query != null) {
            queryURL += "q=" + query;
        }
        if (start != 0) {
            queryURL += "&start=" + start;
        }
        if (subject != null) {
            queryURL += "&subject=" + subject;
        }
        if (resourceType != null) {
            queryURL += "&resource_type=" + resourceType;
        }
        if (educationalContext != null) {
            queryURL += "&educational_context=" + educationalContext;
        }
        if (licenseType != null) {
            queryURL += "&license_type=" + licenseType;
        }
        return queryURL;
    }

    private void assertMaterialIdentifiers(List<Material> materials, Long... materialIdentifiers) {
        assertEquals(materialIdentifiers.length, materials.size());

        for (int i = 0; i < materialIdentifiers.length; i++) {
            assertEquals(materialIdentifiers[i], materials.get(i).getId());
        }
    }

}
