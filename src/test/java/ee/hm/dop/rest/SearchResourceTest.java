package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Material;

public class SearchResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void search() {
        String query = "المدرسية";
        Response response = doGet("search?q=" + query);
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });

        assertEquals(1, materials.size());
        assertEquals(new Long("3"), materials.get(0).getId());
    }

    @Test
    public void searchNoResult() {
        String query = "no+results";
        Response response = doGet("search?q=" + query);
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });

        assertEquals(0, materials.size());
    }
}
