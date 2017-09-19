package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.ReducedMaterial;
import ee.hm.dop.model.ReducedPortfolio;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.enums.EducationalContextC;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;

public class SearchResourceTest extends ResourceIntegrationTestBase {

    private static final int RESULTS_PER_PAGE = 3;

    @Test
    public void search() {
        String query = "المدرسية";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, new SearchFilter()), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 1L);
        assertEquals(1, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchGetSecondPage() {
        String query = "thishasmanyresults";
        int start = RESULTS_PER_PAGE;
        SearchResult searchResult = doGet(buildQueryURL(query, start, null, new SearchFilter()), SearchResult.class);

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
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, new SearchFilter()), SearchResult.class);

        assertEquals(0, searchResult.getItems().size());
    }

    @Test
    public void searchWithNullQueryAndNullFilter() {
        SearchResult searchResult = doGet(buildQueryURL(null, 0, null, new SearchFilter()), SearchResult.class);

        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
        assertMaterialIdentifiers(searchResult.getItems(), 2L, 3L);
    }

    @Test
    public void searchWithNullQueryAndEducationalContextFilter() {
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(1L);
        educationalContext.setName(EducationalContextC.PRESCHOOLEDUCATION);
        searchFilter.setTaxons(Collections.singletonList(educationalContext));
        SearchResult searchResult = doGet(buildQueryURL(null, 0, null, searchFilter), SearchResult.class);

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
        searchFilter.setTaxons(Collections.singletonList(domain));
        String queryURL = buildQueryURL(query, 0, null, searchFilter);
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
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 1L, 3L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithPaidFilterFalse() {
        String query = "dop";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(false);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, searchFilter), SearchResult.class);

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
        SearchResult searchResult = doGet(buildQueryURL(query, start, null, searchFilter), SearchResult.class);

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
        SearchResult searchResult = doGet(buildQueryURL(query, start, null, searchFilter), SearchResult.class);

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
        searchFilter.setTaxons(Collections.singletonList(subject));
        searchFilter.setPaid(false);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, searchFilter), SearchResult.class);

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
        searchFilter.setTaxons(Collections.singletonList(subject));
        searchFilter.setType("material");
        String queryURL = buildQueryURL(query, 0, null, searchFilter);
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
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 1L, 8L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithIssuedFromFilter() {
        String query = "car";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setIssuedFrom(2011);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 5L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithAllFilters() {
        String query = "john";
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(2L);
        educationalContext.setName(EducationalContextC.BASICEDUCATION);
        searchFilter.setTaxons(Collections.singletonList(educationalContext));
        searchFilter.setPaid(false);
        searchFilter.setType("portfolio");
        searchFilter.setIssuedFrom(2011);
        searchFilter.setCurriculumLiterature(true);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, searchFilter), SearchResult.class);
    }

    @Test
    public void searchAsAdmin() {
        login(USER_ADMIN);

        String query = "super";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, new SearchFilter()), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 4L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithLanguageFilter() {
        String query = "monday";
        SearchFilter searchFilter = new SearchFilter();
        Language language = new Language();
        language.setCode("eng");
        searchFilter.setLanguage(language);
        int start = 0;
        SearchResult searchResult = doGet(buildQueryURL(query, start, null, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 1L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(start, searchResult.getStart());
    }

    @Test
    public void searchWithSorting() {
        String query = "tuesday";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setSort("somefield");
        searchFilter.setSortDirection(SearchFilter.SortDirection.DESCENDING);
        int start = 0;
        SearchResult searchResult = doGet(buildQueryURL(query, start, null, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 6L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(start, searchResult.getStart());
    }

    @Test
    public void searchWithCurriculumLiteratureTrue() {
        String query = "data";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(true);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 7L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithCurriculumLiteratureFalse() {
        String query = "data";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(false);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L, 8L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithCurriculumLiteratureFalseAndLimit1() {
        String query = "data";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(false);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, 1L, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 2L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithResourceType() {
        String query = "ditmas";
        SearchFilter searchFilter = new SearchFilter();
        ResourceType resourceType = new ResourceType();
        resourceType.setId(1L);
        resourceType.setName("EXPERIMENT1");
        searchFilter.setResourceType(resourceType);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, 10L, searchFilter), SearchResult.class);

        assertMaterialIdentifiers(searchResult.getItems(), 1L, 6L);
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    private String buildQueryURL(String query, int start, Long limit, SearchFilter searchFilter) {
        String queryURL = "search?";
        if (query != null) {
            queryURL += "q=" + encodeQuery(query);
        }
        if (start != 0) {
            queryURL += "&start=" + start;
        }
        if (limit != null) {
            queryURL += "&limit=" + limit;
        }
        if (searchFilter.getTaxons() != null) {
            for (Taxon taxon : searchFilter.getTaxons()) {
                queryURL += "&taxon=" + taxon.getId();
            }
        }
        if (!searchFilter.isPaid()) {
            queryURL += "&paid=false";
        }
        if (searchFilter.getType() != null) {
            queryURL += "&type=" + encodeQuery(searchFilter.getType());
        }
        if (searchFilter.getResourceType() != null) {
            queryURL += "&resourceType=" + encodeQuery(searchFilter.getResourceType().getName());
        }
        if (searchFilter.getLanguage() != null) {
            queryURL += "&language=" + searchFilter.getLanguage().getCode();
        }
        if (searchFilter.getIssuedFrom() != null) {
            queryURL += "&issuedFrom=" + searchFilter.getIssuedFrom();
        }
        if (searchFilter.isCurriculumLiterature() != null && searchFilter.isCurriculumLiterature()) {
            queryURL += "&curriculumLiterature=true";
        }
        if (searchFilter.getSort() != null) {
            queryURL += "&sort=" + searchFilter.getSort();
        }
        if (searchFilter.getSortDirection() != null) {
            queryURL += "&sortDirection=" + searchFilter.getSortDirection().getValue();
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
            } else if (searchable.getType().equals("reducedportfolio")) {
                assertTrue(searchable instanceof ReducedPortfolio);
            } else if (searchable.getType().equals("reducedmaterial")) {
                assertTrue(searchable instanceof ReducedMaterial);
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
