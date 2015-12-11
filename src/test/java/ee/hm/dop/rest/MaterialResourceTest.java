package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;

public class MaterialResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_NEWEST_MATERIALS_URL = "material/getNewestMaterials?numberOfMaterials=%s";
    private static final String MATERIAL_INCREASE_VIEW_COUNT_URL = "material/increaseViewCount";
    private static final String GET_MATERIAL_PICTURE_URL = "material/getPicture?materialId=%s";
    private static final String GET_MATERIAL_URL = "material?materialId=%s";
    private static final String GET_BY_CREATOR_URL = "material/getByCreator?username=%s";

    @Test
    public void getMaterial() {
        Material material = getMaterial(1);
        assertMaterial1(material);
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

        DateTime added = null;
        for (Material material : materials) {
            if (added != null) {
                added.isAfter(material.getAdded());
            }

            added = material.getAdded();
        }
    }

    @Test
    public void increaseViewCount() {
        long materialId = 5;

        Material materialBefore = getMaterial(materialId);

        Material materialWithOnlyId = new Material();
        materialWithOnlyId.setId(materialId);

        Response response = doPost(MATERIAL_INCREASE_VIEW_COUNT_URL,
                Entity.entity(materialWithOnlyId, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Material materialAfter = getMaterial(materialId);

        assertEquals(Long.valueOf(materialBefore.getViews() + 1), materialAfter.getViews());
    }

    @Test
    public void increaseViewCountNotExistingMaterial() {
        long materialId = 999;

        Response response = doGet(format(GET_MATERIAL_URL, materialId));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

        Material materialWithOnlyId = new Material();
        materialWithOnlyId.setId(materialId);

        response = doPost(MATERIAL_INCREASE_VIEW_COUNT_URL,
                Entity.entity(materialWithOnlyId, MediaType.APPLICATION_JSON_TYPE));
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

        List<Taxon> taxons = material.getTaxons();
        assertNotNull(taxons);
        assertEquals(2, taxons.size());
        Subject biology = (Subject) taxons.get(0);
        assertEquals(new Long(20), biology.getId());
        assertEquals("Biology", biology.getName());
        Subject math = (Subject) taxons.get(1);
        assertEquals(new Long(21), math.getId());
        assertEquals("Mathematics", math.getName());
    }

    @Test
    public void getMaterialWithNoTaxon() {
        Material material = getMaterial(8);
        List<Taxon> taxons = material.getTaxons();
        assertNotNull(taxons);
        assertEquals(0, taxons.size());
    }

    @Test
    public void getByCreator() {
        String username = "mati.maasikas";
        List<Material> materials = doGet(format(GET_BY_CREATOR_URL, username)).readEntity(
                new GenericType<List<Material>>() {
                });

        assertEquals(3, materials.size());
        assertEquals(Long.valueOf(8), materials.get(0).getId());
        assertEquals(Long.valueOf(4), materials.get(1).getId());
        assertEquals(Long.valueOf(1), materials.get(2).getId());
        assertMaterial1(materials.get(2));
    }

    @Test
    public void getByCreatorWithoutUsername() {
        Response response = doGet("material/getByCreator");
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void getByCreatorWithBlankUsername() {
        Response response = doGet(format(GET_BY_CREATOR_URL, ""));
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void getByCreatorNotExistingUser() {
        String username = "notexisting.user";
        Response response = doGet(format(GET_BY_CREATOR_URL, username));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void getByCreatorNoMaterials() {
        String username = "voldemar.vapustav";
        List<Material> materials = doGet(format(GET_BY_CREATOR_URL, username)).readEntity(
                new GenericType<List<Material>>() {
                });

        assertEquals(0, materials.size());
    }

    private void assertMaterial1(Material material) {
        assertEquals(2, material.getTitles().size());
        assertEquals("Matemaatika õpik üheksandale klassile", material.getTitles().get(0).getText());
        assertEquals(2, material.getDescriptions().size());
        assertEquals("Test description in estonian. (Russian available)", material.getDescriptions().get(0).getText());
        Language descriptionLanguage = material.getDescriptions().get(0).getLanguage();
        assertEquals("est", descriptionLanguage.getCode());
        assertNotNull(descriptionLanguage.getName());
        assertNotNull(descriptionLanguage.getCodes());
        Language language = material.getLanguage();
        assertNotNull(language);
        assertEquals("est", language.getCode());
        assertEquals("Estonian", language.getName());
        assertNotNull(language.getCodes());
        assertNull(material.getPicture());
        assertNotNull(material.getTaxons());
        assertEquals(2, material.getTaxons().size());
        assertEquals(new Long(2), material.getTaxons().get(0).getId());
        assertEquals(new Long(20), material.getTaxons().get(1).getId());
        assertNull(material.getRepository());
        assertNull(material.getRepositoryIdentifier());
        assertEquals(new Long(1), material.getCreator().getId());
        assertFalse(material.isEmbeddable());

        assertEquals(2, material.getTargetGroups().size());
        assertTrue(material.getTargetGroups().contains(TargetGroup.ZERO_FIVE));
        assertTrue(material.getTargetGroups().contains(TargetGroup.SIX_SEVEN));
        assertTrue(material.isSpecialEducation());
        assertEquals("Lifelong_learning_and_career_planning", material.getCrossCurricularThemes().get(0).getName());
        assertEquals("Cultural_and_value_competence", material.getKeyCompetences().get(0).getName());
    }

    private Material getMaterial(long materialId) {
        return doGet(format(GET_MATERIAL_URL, materialId), Material.class);
    }
}
