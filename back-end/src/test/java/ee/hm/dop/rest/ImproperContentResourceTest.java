package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.ImproperContent;
import org.junit.Test;

public class ImproperContentResourceTest extends ResourceIntegrationTestBase {

    private static final String IMPROPERS = "impropers";
    public static final String IMPROPER_MATERIALS = "impropers/materials";
    public static final String IMPROPER_PORTFOLIOS = "impropers/portfolios";
    public static final long TEST_PORFOLIO_ID = 101L;

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

    @Test
    public void setImproper() {
        login(USER_ADMIN);

        ImproperContent newImproperContent = doPut(IMPROPERS, improperMaterialContent(1L), ImproperContent.class);

        assertNotNull(newImproperContent);
        assertNotNull(newImproperContent.getId());
        assertEquals((Long) 1L, newImproperContent.getLearningObject().getId());

        Response response = doDelete(format("impropers/%s", newImproperContent.getId()));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void getImpropers() {
        login(USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPERS, genericType());

        assertNotNull(improperContents.size());
        assertEquals(5, improperContents.size());
    }

    @Test
    public void getImproperMaterials() {
        login(USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPER_MATERIALS, genericType());

        assertNotNull(improperContents.size());
        assertEquals(3, improperContents.size());
    }

    @Test
    public void getImproperPortfolios() {
        login(USER_ADMIN);

        List<ImproperContent> improperContents = doGet(IMPROPER_PORTFOLIOS, genericType());

        assertNotNull(improperContents.size());
        assertEquals(2, improperContents.size());
    }

    @Test
    public void getImproperByLearningObject() {
        login(USER_SECOND);

        List<ImproperContent> improperContents = doGet(format("impropers?learningObject=%s", 103L), genericType());

        assertNotNull(improperContents.size());
        assertEquals(1, improperContents.size());
        assertEquals(new Long(5), improperContents.get(0).getId());
    }

    @Test
    public void removeImproperByLearningObject() {
        login(USER_ADMIN);

        doPut(IMPROPERS, improperPortfolioContent(TEST_PORFOLIO_ID), ImproperContent.class);

        Response response = doDelete(format("impropers?learningObject=%s", TEST_PORFOLIO_ID));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        List<ImproperContent> improperContents = doGet(format("impropers?learningObject=%s", TEST_PORFOLIO_ID), genericType());

        assertTrue(improperContents.isEmpty());
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
