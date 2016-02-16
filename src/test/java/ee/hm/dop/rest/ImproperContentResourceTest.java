package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;

/**
 * Created by mart on 11.02.16.
 */
public class ImproperContentResourceTest extends ResourceIntegrationTestBase {

    public static final String IMPROPERS = "impropers";
    public static final String IMPROPERS_WITH_MATERIAL = "impropers?with=material";

    @Test
    public void setImproperNoData() {
        login("89012378912");
        ImproperContent improperContent = new ImproperContent();

        Response response = doPut(IMPROPERS, Entity.entity(improperContent, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        logout();
    }

    @Test
    public void setImproperNoMaterial() {
        login("89012378912");
        ImproperContent improperContent = new ImproperContent();
        Material material = new Material();
        material.setId(34534534L);
        improperContent.setMaterial(material);

        Response response = doPut(IMPROPERS, Entity.entity(improperContent, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        logout();
    }

    @Test
    public void setImproper() {
        ImproperContent improperContent = new ImproperContent();
        Material material = new Material();
        material.setId(1L);
        improperContent.setMaterial(material);
        login("89898989898");

        Response response = doGet("impropers?with=material");
        List<ImproperContent> improperContents = response.readEntity(new GenericType<List<ImproperContent>>() {
        });
        int size = improperContents.size();

        response = doPut(IMPROPERS, Entity.entity(improperContent, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = doGet("impropers/materials/1");
        Boolean bool = response.readEntity(Boolean.class);
        assertTrue(bool);

        response = doDelete("impropers?material=1");
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = doGet(IMPROPERS_WITH_MATERIAL);
        improperContents = response.readEntity(new GenericType<List<ImproperContent>>() {
        });
        assertEquals(size, improperContents.size());

        logout();
    }

    @Test
    public void getImpropers() {
        login("89898989898");
        ImproperContent improperContent = new ImproperContent();
        Material material = new Material();
        material.setId(1L);
        improperContent.setMaterial(material);

        Response response = doGet(IMPROPERS_WITH_MATERIAL);
        List<ImproperContent> improperContents = response.readEntity(new GenericType<List<ImproperContent>>() {
        });

        assertNotNull(improperContents.size());
        assertEquals(3, improperContents.size());

        logout();
    }

    @Test
    public void hasSetImproperMaterial() {
        login("89898989898");

        Response response = doGet("impropers/materials/1");
        Boolean bool = response.readEntity(Boolean.class);
        assertTrue(!bool);

        ImproperContent improperContent = new ImproperContent();
        Material material = new Material();
        material.setId(1L);
        improperContent.setMaterial(material);

        response = doPut(IMPROPERS, Entity.entity(improperContent, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = doGet("impropers/materials/1");
        bool = response.readEntity(Boolean.class);
        assertTrue(bool);

        response = doDelete("impropers?material=1");
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = doGet("impropers/materials/1");
        bool = response.readEntity(Boolean.class);
        assertTrue(!bool);

        logout();
    }

    @Test
    public void deleteImproper() {
        login("89898989898");

        ImproperContent improperContent = new ImproperContent();
        Portfolio portfolio = new Portfolio();
        portfolio.setId(101L);
        improperContent.setPortfolio(portfolio);

        Response response = doPut(IMPROPERS, Entity.entity(improperContent, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = doGet("impropers/portfolios/101");
        Boolean bool = response.readEntity(Boolean.class);
        assertTrue(bool);

        response = doDelete("impropers?portfolio=101");
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = doGet("impropers/portfolios/101");
        bool = response.readEntity(Boolean.class);
        assertTrue(!bool);

        logout();
    }
}
