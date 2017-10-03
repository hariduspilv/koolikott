package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import org.junit.Test;

public class ImproperContentResourceTest extends ResourceIntegrationTestBase {

    public static final String IMPROPERS = "impropers";
    public static final String IMPROPER_MATERIALS = "admin/improper/material";
    public static final String IMPROPER_MATERIALS_COUNT = "admin/improper/material/count";
    public static final String IMPROPER_PORTFOLIOS = "admin/improper/portfolio";
    public static final String IMPROPER_PORTFOLIOS_COUNT = "admin/improper/portfolio/count";
    public static final String GET_IMPROPERS_BY_ID = "impropers/%s";
    public static final Long TEST_PORFOLIO_ID = TestConstants.PORTFOLIO_1;
    public static final Long TEST_UNREVIEWED_PORFOLIO_ID = TestConstants.PORTFOLIO_8;

    @Test
    public void setImproperNoData() {
        login(TestConstants.USER_SECOND);

        Response response = doPut(IMPROPERS, new ImproperContent());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void setImproperNotExistemLearningObject() {
        login(TestConstants.USER_SECOND);

        Response response = doPut(IMPROPERS, improperMaterialContent(34534534L));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void setImproper() {
        login(TestConstants.USER_ADMIN);

        ImproperContent newImproperContent = doPut(IMPROPERS, improperMaterialContent(TestConstants.MATERIAL_1), ImproperContent.class);

        assertNotNull(newImproperContent);
        assertNotNull(newImproperContent.getId());
        assertEquals(TestConstants.MATERIAL_1, newImproperContent.getLearningObject().getId());

        Response response = doDelete(format(GET_IMPROPERS_BY_ID, newImproperContent.getId()));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void getImpropers() {
        login(TestConstants.USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPERS, genericType());

        assertNotNull(improperContents.size());
        assertEquals(5, improperContents.size());
    }

    @Test
    public void getImproperMaterials() {
        login(TestConstants.USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPER_MATERIALS, genericType());

        assertNotNull(improperContents.size());
        long uniqueLearningObjIdsCount = improperContents.stream()
                .map(ImproperContent::getLearningObject)
                .map(LearningObject::getId)
                .distinct()
                .count();
        assertEquals(3, improperContents.size());

        long materialsCount = doGet(IMPROPER_MATERIALS_COUNT, Long.class);
        assertEquals(uniqueLearningObjIdsCount, materialsCount);
    }

    @Test
    public void getImproperPortfolios() {
        login(TestConstants.USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPER_PORTFOLIOS, genericType());

        assertNotNull(improperContents.size());
        assertEquals(2, improperContents.size());

        long portfoliosCount = doGet(IMPROPER_PORTFOLIOS_COUNT, Long.class);
        assertEquals(improperContents.size(), portfoliosCount);
    }

    @Test
    public void getImproperByLearningObject() {
        login(TestConstants.USER_SECOND);

        List<ImproperContent> improperContents = doGet(format(GET_IMPROPERS_BY_ID, TestConstants.PORTFOLIO_3), genericType());

        assertNotNull(improperContents.size());
        assertEquals(1, improperContents.size());
        assertEquals(new Long(5), improperContents.get(0).getId());
    }

    @Test
    public void approving_improper_content_removes_it() {
        login(TestConstants.USER_ADMIN);

        doPut(IMPROPERS, improperPortfolioContent(TEST_PORFOLIO_ID), ImproperContent.class);

        Response response = doDelete(format("impropers?learningObject=%s", TEST_PORFOLIO_ID));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        List<ImproperContent> improperContents = doGet(format(GET_IMPROPERS_BY_ID, TEST_PORFOLIO_ID), genericType());

        assertTrue(improperContents.isEmpty());
    }

    @Test
    public void approving_unreviewed_improper_content_removes_improper_content_and_sets_it_reviewed() {
        login(TestConstants.USER_ADMIN);

        doPut(IMPROPERS, improperPortfolioContent(TEST_UNREVIEWED_PORFOLIO_ID), ImproperContent.class);

        Response response = doDelete(format("impropers?learningObject=%s", TEST_UNREVIEWED_PORFOLIO_ID));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        List<ImproperContent> improperContents = doGet(format(GET_IMPROPERS_BY_ID, TEST_UNREVIEWED_PORFOLIO_ID), genericType());

        assertTrue(improperContents.isEmpty());

        Portfolio portfolio = getPortfolio(TEST_UNREVIEWED_PORFOLIO_ID);
        assertTrue(portfolio.getUnReviewed() == 0);
    }

    private GenericType<List<ImproperContent>> genericType() {
        return new GenericType<List<ImproperContent>>() {
        };
    }

    private ImproperContent improperMaterialContent(long id) {
        ImproperContent improperContent = new ImproperContent();
        improperContent.setLearningObject(materialWithId(id));
        return improperContent;
    }

    private ImproperContent improperPortfolioContent(long id) {
        ImproperContent improperContent = new ImproperContent();
        improperContent.setLearningObject(portfolioWithId(id));
        return improperContent;
    }
}
