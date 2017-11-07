package ee.hm.dop.rest.solr;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestConstants;
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
import ee.hm.dop.model.enums.LanguageC;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;

public class SearchResourceTest extends ResourceIntegrationTestBase {

    private static final int RESULTS_PER_PAGE = 3;

    @Test
    public void getMostLiked() throws Exception {
        List<Searchable> results = doGet("search/mostLiked?maxResults=12", new GenericType<List<Searchable>>() {
        });
        assertTrue(CollectionUtils.isNotEmpty(results));
    }

    @Test
    public void search() {
        String query = "المدرسية";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, new SearchFilter()), SearchResult.class);

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_1);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
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
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_2, TestConstants.MATERIAL_3);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_2, MATERIAL_3);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
    }

    @Test
    public void searchWithNullQueryAndEducationalContextFilter() {
        SearchFilter searchFilter = new SearchFilter();
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(1L);
        educationalContext.setName(EducationalContextC.PRESCHOOLEDUCATION);
        searchFilter.setTaxons(Collections.singletonList(educationalContext));
        SearchResult searchResult = doGet(buildQueryURL(null, 0, null, searchFilter), SearchResult.class);

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_2);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_2);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
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

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_1, TestConstants.MATERIAL_2);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_1, MATERIAL_2);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithPaidFilterTrue() {
        String query = "dop";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(true);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, searchFilter), SearchResult.class);

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_1, TestConstants.MATERIAL_3);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_1, MATERIAL_3);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithPaidFilterFalse() {
        String query = "dop";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPaid(false);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, searchFilter), SearchResult.class);

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_1, TestConstants.MATERIAL_4);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_1, MATERIAL_4);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
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

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_1, TestConstants.MATERIAL_2, TestConstants.MATERIAL_3);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_1, MATERIAL_2, MATERIAL_3);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
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

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_1, TestConstants.MATERIAL_5);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_1, MATERIAL_5);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
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

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_1, TestConstants.MATERIAL_6);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_1, MATERIAL_6);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
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

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_1, TestConstants.MATERIAL_7);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_1, MATERIAL_7);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
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

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_1, TestConstants.MATERIAL_8);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_1, MATERIAL_8);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithIssuedFromFilter() {
        String query = "car";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setIssuedFrom(2011);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, searchFilter), SearchResult.class);

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_2, TestConstants.MATERIAL_5);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_2, MATERIAL_5);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
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
        //todo missing assertions
    }

    @Test
    public void searchAsAdmin() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        login(TestConstants.USER_ADMIN);
=======
        login(USER_ADMIN);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java

        String query = "super";
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, new SearchFilter()), SearchResult.class);

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_2, TestConstants.MATERIAL_4);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_2, MATERIAL_4);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithLanguageFilter() {
        String query = "monday";
        SearchFilter searchFilter = new SearchFilter();
        Language language = new Language();
        language.setCode(LanguageC.ENG);
        searchFilter.setLanguage(language);
        int start = 0;
        SearchResult searchResult = doGet(buildQueryURL(query, start, null, searchFilter), SearchResult.class);

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_2, TestConstants.MATERIAL_1);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_2, MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
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

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_2, TestConstants.MATERIAL_6);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_2, MATERIAL_6);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(start, searchResult.getStart());
    }

    @Test
    public void searchWithCurriculumLiteratureTrue() {
        String query = "data";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(true);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, searchFilter), SearchResult.class);

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_2, TestConstants.MATERIAL_7);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_2, MATERIAL_7);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithCurriculumLiteratureFalse() {
        String query = "data";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(false);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, null, searchFilter), SearchResult.class);

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_2, TestConstants.MATERIAL_8);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_2, MATERIAL_8);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
        assertEquals(2, searchResult.getTotalResults());
        assertEquals(0, searchResult.getStart());
    }

    @Test
    public void searchWithCurriculumLiteratureFalseAndLimit1() {
        String query = "data";
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setCurriculumLiterature(false);
        SearchResult searchResult = doGet(buildQueryURL(query, 0, 1L, searchFilter), SearchResult.class);

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_2);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_2);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
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

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/rest/SearchResourceTest.java
        assertMaterialIdentifiers(searchResult.getItems(), TestConstants.MATERIAL_1, TestConstants.MATERIAL_6);
=======
        assertMaterialIdentifiers(searchResult.getItems(), MATERIAL_1, MATERIAL_6);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/rest/solr/SearchResourceTest.java
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

    private void assertMaterialIdentifiers(List<Searchable> objects, Long... learningObjectIds) {
        assertEquals(learningObjectIds.length, objects.size());

        for (int i = 0; i < learningObjectIds.length; i++) {
            Searchable searchable = objects.get(i);
            assertEquals(learningObjectIds[i], searchable.getId());

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
