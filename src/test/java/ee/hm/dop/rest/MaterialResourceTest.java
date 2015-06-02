package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.joda.time.LocalDate;
import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.Material;

public class MaterialResourceTest extends ResourceIntegrationTestBase {

    @Test
    public void getAllMaterials() {
        Response response = doGet("material/getAll");
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });

        assertEquals(8, materials.size());

        // Verify if all fields are loaded
        Material material = materials.get(1);
        assertEquals(2, material.getId());
        assertEquals("Mathematics textbook for 8th grade", material.getTitle());
        assertEquals(new LocalDate(-983, 1, 27), material.getIssueDate());
        List<Author> authors = material.getAuthors();
        assertEquals(2, authors.size());

        boolean newton = false, fibonacci = false;
        for (Author author : authors) {
            if (author.getId() == 1) {
                assertEquals("Isaac", author.getName());
                assertEquals("Newton", author.getSurname());
                newton = true;
            } else if (author.getId() == 3) {
                assertEquals("Leonardo", author.getName());
                assertEquals("Fibonacci", author.getSurname());
                fibonacci = true;
            }
        }

        assertTrue(fibonacci && newton);
    }
}
