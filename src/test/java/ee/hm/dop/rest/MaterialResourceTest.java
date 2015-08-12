package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Subject;

public class MaterialResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_NEWEST_MATERIALS_URL = "material/getNewestMaterials?numberOfMaterials=%s";
    private static final String MATERIAL_INCREASE_VIEW_COUNT_URL = "material/increaseViewCount";
    private static final String GET_MATERIAL_PICTURE_URL = "material/getPicture?materialId=%s";
    private static final String GET_MATERIAL_URL = "material?materialId=%s";

    @Test
    public void getMaterial() {
        Material material = getMaterial(1);

        assertEquals(2, material.getTitles().size());
        assertEquals("Matemaatika õpik üheksandale klassile", material.getTitles().get(0).getText());
        assertEquals(2, material.getDescriptions().size());
        assertEquals("Test description in estonian. (Russian available)", material.getDescriptions().get(0).getText());
        Language language = material.getDescriptions().get(0).getLanguage();
        assertNotNull(language);
        assertEquals("est", language.getCode());
        assertNull(language.getName());
        assertNull(language.getCodes());
        assertNull(material.getPicture());
        assertNotNull(material.getSubjects());
        assertEquals(1, material.getSubjects().size());
        assertEquals(new Long(1), material.getSubjects().get(0).getId());
        assertNull(material.getRepository());
        assertNull(material.getRepositoryIdentifier());
    }

    @Test
    public void getMaterialDescriptionAndLanguage() {
        Material material = getMaterial(1);

        List<LanguageString> descriptions = material.getDescriptions();
        assertEquals(2, descriptions.size());
        for (LanguageString languageString : descriptions) {
            if (languageString.getId() == 1) {
                assertEquals("est", languageString.getLanguage().getCode());
                assertEquals("Test description in estonian. (Russian available)", languageString.getText());
            } else if (languageString.getId() == 2) {
                assertEquals("est", languageString.getLanguage().getCode());
                assertEquals("Test description in russian, which is the only language available.",
                        languageString.getText());

            }
        }
    }

    @Test
    public void getMaterialEducationalContext() {
        Material material = getMaterial(1);
        assertEquals("PRESCHOOL", material.getEducationalContexts().get(0).getName());
    }

    @Test
    public void getMaterialLicenseType() {
        Material material = getMaterial(1);
        assertEquals("CCBY", material.getLicenseType().getName());
    }

    @Test
    public void getMaterialPublisher() {
        Material material = getMaterial(1);
        assertEquals("Koolibri", material.getPublishers().get(0).getName());
    }

    @Test
    public void getMaterialAddedDate() {
        Material material = getMaterial(1);
        assertEquals(new DateTime("1999-01-01T02:00:01.000+02:00"), material.getAdded());
    }

    @Test
    public void getMaterialUpdatedDate() {
        Material material = getMaterial(2);
        assertEquals(new DateTime("1995-07-12T09:00:01.000+00:00"), material.getUpdated());
    }

    @Test
    public void getMaterialTags() {
        Material material = getMaterial(1);

        assertEquals(5, material.getTags().size());
        assertEquals("matemaatika", material.getTags().get(0).getName());
        assertEquals("põhikool", material.getTags().get(1).getName());
        assertEquals("õpik", material.getTags().get(2).getName());
        assertEquals("mathematics", material.getTags().get(3).getName());
        assertEquals("book", material.getTags().get(4).getName());

    }

    @Test
    public void GetNewestMaterials() {
        Response response = doGet(format(GET_NEWEST_MATERIALS_URL, 8));

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

        Material materialBefore = getMaterial(materialId);

        Response response = doPost(MATERIAL_INCREASE_VIEW_COUNT_URL,
                Entity.entity(materialId, MediaType.APPLICATION_JSON));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Material materialAfter = getMaterial(materialId);

        assertEquals(Long.valueOf(materialBefore.getViews() + 1), materialAfter.getViews());
    }

    @Test
    public void increaseViewCountNotExistingMaterial() {
        long materialId = 999;

        Response response = doGet(format(GET_MATERIAL_URL, materialId));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = doPost(MATERIAL_INCREASE_VIEW_COUNT_URL, Entity.entity(materialId, MediaType.APPLICATION_JSON));
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        response = doGet(format(GET_MATERIAL_URL, materialId));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void getMaterialPicture() {
        long materialId = 1;
        Response response = doGet(format(GET_MATERIAL_PICTURE_URL, materialId), MediaType.WILDCARD_TYPE);
        byte[] picture = response.readEntity(new GenericType<byte[]>() {
        });
        assertNotNull(picture);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void getMaterialPictureNull() {
        long materialId = 999;
        Response response = doGet(format(GET_MATERIAL_PICTURE_URL, materialId), MediaType.WILDCARD_TYPE);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getMaterialWithSubjects() {
        Material material = getMaterial(6);

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
        Material material = getMaterial(7);
        List<Subject> subjects = material.getSubjects();
        assertNotNull(subjects);
        assertEquals(0, subjects.size());
    }

    private Material getMaterial(long materialId) {
        return doGet(format(GET_MATERIAL_URL, materialId), Material.class);
    }
}
