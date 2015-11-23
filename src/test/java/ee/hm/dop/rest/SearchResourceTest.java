package ee.hm.dop.rest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Ignore;
import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Domain;
import ee.hm.dop.model.EducationalContext;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.Subject;

@Ignore
public class SearchResourceTest extends ResourceIntegrationTestBase {

    private static final int RESULTS_PER_PAGE = 3;

    @Test
    public void search() {
        String query = "المدرسية";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, new SearchFilter()), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 1L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchGetSecondPage() {
        String query = "thishasmanyresults";
        int start = RESULTS_PER_PAGE;
        SearchResult searchResult = doGet(buildQueryURL(query, start, new SearchFilter()), SearchResult.class);

        assertEquals(RESULTS_PER_PAGE, searchResult.getItems().size());
        for (int i = 0; i < RESULTS_PER_PAGE; i++) {
            assertEquals(Long.valueOf(i + start), searchResult.getItems().get(i).getId());
        }
        assertEquals(8, searchResult.getTotalResults());
        assertEquals(start, searchResult.getStart());
    }

    @Test
    public void searchNoResult() {
        String query = "no+results";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, new SearchFilter()), SearchResult.class);

        assertEquals(0, searchResult.getItems().size());
    }

    @Test
    public void searchWithNullQueryAndNullFilter() {
        Response response = doGet(buildQueryURL(null, 0, new SearchFilter()));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void searchWithNullQueryAndEducationalContextFilter() {
        String query = null;
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(1L);
        educationalContext.setName("PRESCHOOLEDUCATION");
        searchFilter.setTaxon(educationalContext);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithTaxonDomainFilter() {
        String query = "beethoven";
        SearchFilter searchFilter = new SearchFilter();
        Domain domain = new Domain();
        domain.setId(10L);
        domain.setName("Mathematics");
        searchFilter.setTaxon(domain);
        String queryURL = buildQueryURL(query, 0, searchFilter);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 1L, 2L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithPaidFilterTrue() {
        String query = "dop";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(true);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 1L, 3L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithPaidFilterFalse() {
        String query = "dop";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(false);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 1L, 4L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithTypeFilter() {
        String query = "weird";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType("portfolio");
        int start = 0;
        SearchResult searchResult = doGet(buildQueryURL(query, start, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 1L, 2L, 3L);
        assertEquals(3, searchResult.getTotalResults());
        assertEquals(start, searchResult.getStart());
    }

    @Test
    public void searchWithTypeFilterAll() {
        String query = "weird";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setType("all");
        int start = 0;
        SearchResult searchResult = doGet(buildQueryURL(query, start, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 1L, 5L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(start, searchResult.getStart());
    }

    @Test
    public void searchWithTaxonSubjectAndPaidFilterFalse() {
        String query = "dop";
        SearchFilter searchFilter = new SearchFilter();
        Subject subject = new Subject();
        subject.setId(20L);
        subject.setName("Biology");
        searchFilter.setTaxon(subject);
        searchFilter.setPaid(false);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 1L, 6L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithTaxonSubjectAndTypeFilter() {
        String query = "beethoven";
        SearchFilter searchFilter = new SearchFilter();
        Subject subject = new Subject();
        subject.setId(21L);
        subject.setName("Mathematics");
        searchFilter.setTaxon(subject);
        searchFilter.setType("material");
        String queryURL = buildQueryURL(query, 0, searchFilter);
        SearchResult searchResult = doGet(queryURL, SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 1L, 7L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithPaidFalseAndTypeFilter() {
        String query = "weird";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(false);
        searchFilter.setType("material");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 1L, 8L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithAllFilters() {
        String query = "john";
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName("BASICEDUCATION");
        searchFilter.setTaxon(educationalContext);
        searchFilter.setPaid(false);
        searchFilter.setType("portfolio");
        SearchResult searchResult = doGet(buildQueryURL(query, 0, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 3L, 4L);
        assertEquals(3, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    private String buildQueryURL(String query, int start, SearchFilter searchFilter) {
        String queryURL = "search?";
        if (query != null) {
            queryURL += "q=" + encodeQuery(query);
        }
        if (start != 0) {
            queryURL += "&start=" + start;
        }
        if (searchFilter.getTaxon() != null) {
            queryURL += "&taxon=" + searchFilter.getTaxon().getId();
        }
        if (searchFilter.isPaid() == false) {
            queryURL += "&paid=false";
        }
        if (searchFilter.getType() != null) {
            queryURL += "&type=" + encodeQuery(searchFilter.getType());
        }
        return queryURL;
    }

    private void assertMaterialIdentifiers(List<Searchable> objects, Long... materialIdentifiers) {
        assertEquals(materialIdentifiers.length, objects.size());

        for (int i = 0; i < materialIdentifiers.length; i++) {
            Searchable searchable = objects.get(i);
            assertEquals(materialIdentifiers[i], searchable.getId());

            if (searchable.getType().equals("material")) {
                assertTrue(searchable instanceof Material);
            } else if (searchable.getType().equals("portfolio")) {
                assertTrue(searchable instanceof Portfolio);
            } else {
                fail("No such Searchable type: " + searchable.getType());
            }
        }
    }

    private String encodeQuery(String query) {
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return encodedQuery;
    }

}
