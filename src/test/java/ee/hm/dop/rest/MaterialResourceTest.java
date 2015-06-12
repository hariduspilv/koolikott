package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.IssueDate;
import ee.hm.dop.model.LanguageString;
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
        assertEquals(Long.valueOf(2), material.getId());
        assertEquals("?????????? ??????? ??? 8-?? ??????", material.getTitle());
        assertEquals(new IssueDate((short) 27, (short) 1, -983), material.getIssueDate());
        List<Author> authors = material.getAuthors();
        assertEquals(2, authors.size());

        boolean newton = false, fibonacci = false;
        for (Author author : authors) {
            if (author.getId() == 1) {
                assertEquals("Isaac mart", author.getName());
                assertEquals("samjang Newton", author.getSurname());
                newton = true;
            } else if (author.getId() == 3) {
                assertEquals("Leonardo", author.getName());
                assertEquals("Fibonacci", author.getSurname());
                fibonacci = true;
            }
        }

        assertTrue(fibonacci && newton);
    }

    @Test
    public void getMaterialDescriptionAndLanguage() {
        Response response = doGet("material/getAll");
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });
        Material material = materials.get(0);

        List<LanguageString> descriptions = material.getDescriptions();
        assertEquals(2, descriptions.size());
        for (LanguageString languageString : descriptions) {
            if (languageString.getId() == 1) {
                assertEquals("est", languageString.getLanguage());
                assertEquals("test description in estonian", languageString.getText());
            } else if (languageString.getId() == 2) {
                assertEquals("est", languageString.getLanguage());
                assertEquals("test description in russian", languageString.getText());

            }
        }
    }
}
