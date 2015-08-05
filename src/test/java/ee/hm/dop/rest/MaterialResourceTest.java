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
import javax.ws.rs.core.Response.Status;

import org.joda.time.DateTime;
import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.IssueDate;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Subject;

public class MaterialResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_MATERIAL_URL = "material?materialId=%s";

    @Test
    public void getAllMaterials() {
        Response response = doGet("material/getAll");

        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });

        assertEquals(8, materials.size());

        // Verify if all fields are loaded
        Material material = materials.get(1);
        assertEquals(Long.valueOf(2), material.getId());
        assertEquals("Математика учебник для 8-го класса", material.getTitles().get(0).getText());
        assertEquals(new IssueDate((short) 27, (short) 1, -983), material.getIssueDate());
        List<Author> authors = material.getAuthors();
        assertEquals(2, authors.size());
        assertEquals(Long.valueOf(200), material.getViews());

        boolean newton = false, fibonacci = false;
        for (Author author : authors) {
            if (author.getId() == 1) {
                assertEquals("Isaac", author.getName());
                assertEquals("John Newton", author.getSurname());
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
                assertEquals("est", languageString.getLanguage().getCode());
                assertEquals("Test description in estonian. (Russian available)", languageString.getText());
            } else if (languageString.getId() == 2) {
                assertEquals("est", languageString.getLanguage());
                assertEquals("Test description in russian, which is the only language available.",
                        languageString.getText());

            }
        }
    }

    @Test
    public void getMaterialEducationalContext() {
        Response response = doGet("material/getAll");
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });
        Material material = materials.get(0);

        assertEquals("PRESCHOOL", material.getEducationalContexts().get(0).getName());
    }

    @Test
    public void getMaterialLicenseType() {
        Response response = doGet("material/getAll");
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });
        Material material = materials.get(0);

        assertEquals("CCBY", material.getLicenseType().getName());
    }

    @Test
    public void getMaterialPublisher() {
        Response response = doGet("material/getAll");
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });
        Material material = materials.get(0);

        assertEquals("Koolibri", material.getPublishers().get(0).getName());
    }

    @Test
    public void getMaterialAddedDate() {
        Response response = doGet("material/getAll");
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });
        Material material = materials.get(0);
        assertEquals(new DateTime("1999-01-01T02:00:01.000+02:00"), material.getAdded());
    }

    @Test
    public void getMaterialUpdatedDate() {
        Response response = doGet("material/getAll");
        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });
        Material material = materials.get(1);
        assertEquals(new DateTime("1995-07-12T09:00:01.000+00:00"), material.getUpdated());
    }

    @Test
    public void getMaterialTags() {
        long materialId = 1;
        Response response = doGet(format(GET_MATERIAL_URL, materialId));
        Material material = response.readEntity(new GenericType<Material>() {
        });

        assertEquals(5, material.getTags().size());

        assertEquals("matemaatika", material.getTags().get(0).getName());
        assertEquals("põhikool", material.getTags().get(1).getName());
        assertEquals("õpik", material.getTags().get(2).getName());
        assertEquals("mathematics", material.getTags().get(3).getName());
        assertEquals("book", material.getTags().get(4).getName());

    }

    @Test
    public void GetNewestMaterials() {
        Response response = doGet("material/getNewestMaterials?numberOfMaterials=8");

        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });

        assertEquals(8, materials.size());

        Material material = materials.get(0);
        assertEquals(Long.valueOf(8), material.getId());
        assertEquals("Aabits 123", material.getTitles().get(0).getText());

        List<Author> authors = material.getAuthors();
        assertEquals(1, authors.size());
        assertEquals("Karl Simon Ben", material.getAuthors().get(0).getName());
        assertEquals("Tom Oliver Marx", material.getAuthors().get(0).getSurname());
    }

    @Test
    public void increaseViewCount() {
        long materialId = 5;

        Response response = doGet(format(GET_MATERIAL_URL, materialId));
        Material material = response.readEntity(new GenericType<Material>() {
        });
        Long previousViewCount = material.getViews();

        response = doPost("material/increaseViewCount", Entity.entity(materialId, MediaType.APPLICATION_JSON));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        response = doGet(format(GET_MATERIAL_URL, materialId));
        material = response.readEntity(new GenericType<Material>() {
        });
        Long newViewCount = material.getViews();

        assertEquals(Long.valueOf(previousViewCount + 1), newViewCount);
    }

    @Test
    public void increaseViewCountNotExistingMaterial() {
        long materialId = 999;

        Response response = doGet(format(GET_MATERIAL_URL, materialId));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = doPost("material/increaseViewCount", Entity.entity(materialId, MediaType.APPLICATION_JSON));
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        response = doGet(format(GET_MATERIAL_URL, materialId));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void getMaterialPicture() {
        long materialId = 1;
        Response response = doGet("material/getPicture?materialId=" + materialId, MediaType.WILDCARD_TYPE);
        byte[] picture = response.readEntity(new GenericType<byte[]>() {
        });
        assertNotNull(picture);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void getMaterialPictureNull() {
        long materialId = 999;
        Response response = doGet("material/getPicture?materialId=" + materialId, MediaType.WILDCARD_TYPE);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getMaterialWithSubjects() {
        long materialId = 6;
        Material material = doGet(format(GET_MATERIAL_URL, materialId), Material.class);

        List<Subject> subjects = material.getSubjects();
        assertNotNull(subjects);
        assertEquals(2, subjects.size());
        Subject biology = subjects.get(0);
        assertEquals(new Long(1), biology.getId());
        assertEquals("Biology", biology.getName());
        Subject math = subjects.get(1);
        assertEquals(new Long(2), math.getId());
        assertEquals("Mathematics", math.getName());
    }

    @Test
    public void getMaterialWithNoSubject() {
        long materialId = 7;
        Material material = doGet(format(GET_MATERIAL_URL, materialId), Material.class);
        List<Subject> subjects = material.getSubjects();
        assertNotNull(subjects);
        assertEquals(0, subjects.size());
    }
}
