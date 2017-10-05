package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class ImproperContentResourceTest extends ResourceIntegrationTestBase {

    public static final String IMPROPERS = "impropers";
    public static final String IMPROPER_MATERIALS = "admin/improper/material";
    public static final String IMPROPER_MATERIALS_COUNT = "admin/improper/material/count";
    public static final String IMPROPER_PORTFOLIOS = "admin/improper/portfolio";
    public static final String IMPROPER_PORTFOLIOS_COUNT = "admin/improper/portfolio/count";
    public static final String GET_IMPROPERS_BY_ID = "impropers/%s";
    public static final String REVIEW_IMPROPERS_BY_ID = "impropers?learningObject=%s";
    public static final Long TEST_PORFOLIO_ID = PORTFOLIO_1;
    public static final Long TEST_UNREVIEWED_PORFOLIO_ID = PORTFOLIO_8;

    @Test
    public void setImproperNoData() {
        login(USER_SECOND);

        Response response = doPut(IMPROPERS, new ImproperContent());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void setImproperNotExistemLearningObject() {
        login(USER_SECOND);

        Response response = doPut(IMPROPERS, improperMaterialContent(34534534L));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Ignore
    @Test
    public void setImproper() {
        login(USER_ADMIN);

        ImproperContent newImproperContent = doPut(IMPROPERS, improperMaterialContent(MATERIAL_1), ImproperContent.class);

        assertNotNull(newImproperContent);
        assertNotNull(newImproperContent.getId());
        assertEquals(MATERIAL_1, newImproperContent.getLearningObject().getId());

        Response response = doDelete(format(REVIEW_IMPROPERS_BY_ID, newImproperContent.getId()));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void getImpropers() {
        login(USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPERS, genericType());

        assertNotNull(improperContents.size());
        assertTrue(CollectionUtils.isNotEmpty(improperContents));
    }

    @Test
    public void getImproperMaterials() {
        login(USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPER_MATERIALS, genericType());

        assertNotNull(improperContents.size());
        long uniqueLearningObjIdsCount = improperContents.stream()
                .map(ImproperContent::getLearningObject)
                .map(LearningObject::getId)
                .distinct()
                .count();
        assertTrue(CollectionUtils.isNotEmpty(improperContents));

        long materialsCount = doGet(IMPROPER_MATERIALS_COUNT, Long.class);
        assertEquals(uniqueLearningObjIdsCount, materialsCount);
    }

    @Test
    public void getImproperPortfolios() {
        login(USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPER_PORTFOLIOS, genericType());

        assertNotNull(improperContents.size());
        assertEquals(2, improperContents.size());

        long portfoliosCount = doGet(IMPROPER_PORTFOLIOS_COUNT, Long.class);
        assertEquals(improperContents.size(), portfoliosCount);
    }

    @Test
    public void getImproperByLearningObject() {
        login(USER_SECOND);

        List<ImproperContent> improperContents = doGet(format(GET_IMPROPERS_BY_ID, PORTFOLIO_3), genericType());

        assertNotNull(improperContents.size());
        assertEquals(1, improperContents.size());
        assertEquals(new Long(5), improperContents.get(0).getId());
    }

    @Test
    public void approving_improper_content_removes_it() {
        login(USER_ADMIN);

        doPut(IMPROPERS, improperPortfolioContent(TEST_PORFOLIO_ID), ImproperContent.class);

        Response response = doDelete(format("impropers?learningObject=%s", TEST_PORFOLIO_ID));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        List<ImproperContent> improperContents = doGet(format(GET_IMPROPERS_BY_ID, TEST_PORFOLIO_ID), genericType());

        assertTrue(improperContents.isEmpty());
    }

    @Test
    public void approving_unreviewed_improper_content_removes_improper_content_and_sets_it_reviewed() {
        login(USER_ADMIN);

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
