package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import org.junit.Test;

public class ImproperContentResourceTest extends ResourceIntegrationTestBase {

    private static final String IMPROPERS = "impropers";

    @Test
    public void setImproperNoData() {
        login("89012378912");
        ImproperContent improperContent = new ImproperContent();

        Response response = doPut(IMPROPERS, Entity.entity(improperContent, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void setImproperNotExistemLearningObject() {
        login("89012378912");
        ImproperContent improperContent = new ImproperContent();
        Material material = new Material();
        material.setId(34534534L);
        improperContent.setLearningObject(material);

        Response response = doPut(IMPROPERS, Entity.entity(improperContent, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void setImproper() {
        login("89898989898");

        ImproperContent improperContent = new ImproperContent();

        Long materialId = 1L;
        Material material = new Material();
        material.setId(materialId);
        improperContent.setLearningObject(material);

        ImproperContent newImproperContent = doPut(IMPROPERS,
                Entity.entity(improperContent, MediaType.APPLICATION_JSON_TYPE), ImproperContent.class);

        assertNotNull(newImproperContent);
        assertNotNull(newImproperContent.getId());
        assertEquals(materialId, newImproperContent.getLearningObject().getId());

        Response response = doDelete(format("impropers/%s", newImproperContent.getId()));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void getImpropers() {
        login("89898989898");

        Response response = doGet(IMPROPERS);
        List<ImproperContent> improperContents = response.readEntity(new GenericType<List<ImproperContent>>() {
        });

        assertNotNull(improperContents.size());
        assertEquals(5, improperContents.size());
    }

    @Test
    public void getImproperByLearningObject() {
        login("89012378912");

        Response response = doGet(format("impropers?learningObject=%s", 103L));
        List<ImproperContent> improperContents = response.readEntity(new GenericType<List<ImproperContent>>() {
        });

        assertNotNull(improperContents.size());
        assertEquals(1, improperContents.size());
        assertEquals(new Long(5), improperContents.get(0).getId());
    }

    @Test
    public void removeImproperByLearningObject() {
        login("89898989898");

        ImproperContent improperContent = new ImproperContent();
        Portfolio portfolio = new Portfolio();
        portfolio.setId(101L);
        improperContent.setLearningObject(portfolio);

        doPut(IMPROPERS, Entity.entity(improperContent, MediaType.APPLICATION_JSON_TYPE), ImproperContent.class);

        Response response = doDelete(format("impropers?learningObject=%s", portfolio.getId()));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = doGet(format("impropers?learningObject=%s", portfolio.getId()));
        List<ImproperContent> improperContents = response.readEntity(new GenericType<List<ImproperContent>>() {
        });

        assertTrue(improperContents.isEmpty());
    }
}
