package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Material;

public class SearchResourceTest extends ResourceIntegrationTestBase {

    private static final int RESULTS_PER_PAGE = 3;
    
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
    public void searchGetSecondPage() {
        String query = "thishasmanyresults";
        int start = RESULTS_PER_PAGE;
        Response response = doGet("search?q=" + query + "&start=" + start);
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });
        
        assertEquals(RESULTS_PER_PAGE, materials.size());
        
        for (int i = 0; i < RESULTS_PER_PAGE; i++) {
            assertEquals(Long.valueOf(i + start), materials.get(i).getId());
        }
    }

    @Test
    public void searchNoResult() {
        String query = "no+results";
        Response response = doGet("search?q=" + query);
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });

        assertEquals(0, materials.size());
    }
    
    @Test
    public void countResults() {
        String query = "thishasmanyresults";
        Response response = doGet("search/countResults?q=" + query);
        long result = response.readEntity(new GenericType<Long>() {
        });

        assertEquals(8, result);
    }
}
