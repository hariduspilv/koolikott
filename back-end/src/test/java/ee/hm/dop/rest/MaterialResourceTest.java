package ee.hm.dop.rest;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.dao.TaxonDAO;
import ee.hm.dop.model.*;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.MaterialService;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

public class MaterialResourceTest extends ResourceIntegrationTestBase {

    private static final String MATERIAL_INCREASE_VIEW_COUNT_URL = "material/increaseViewCount";
    private static final String GET_MATERIAL_URL = "material?materialId=%s";
    private static final String GET_BY_CREATOR_URL = "material/getByCreator?username=%s";
    private static final String CREATE_MATERIAL_URL = "material";
    private static final String MATERIAL_SET_BROKEN = "material/setBroken";
    private static final String MATERIAL_GET_BROKEN = "material/getBroken";
    private static final String MATERIAL_SET_NOT_BROKEN = "material/setNotBroken";
    private static final String MATERIAL_HAS_SET_BROKEN = "material/hasSetBroken";
    private static final String MATERIAL_IS_BROKEN = "material/isBroken";
    private static final String MATERIAL_ADD_RECOMMENDATION = "material/recommend";
    private static final String MATERIAL_REMOVE_RECOMMENDATION = "material/removeRecommendation";
    private static final String RESTORE_MATERIAL = "material/restore";

    @Inject
    private MaterialService materialService;

    @Inject
    private TaxonDAO taxonDAO;

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
        SearchResult result = doGet(format(GET_BY_CREATOR_URL, username), SearchResult.class);

        assertEquals(3, result.getItems().size());
        assertEquals(Long.valueOf(8), result.getItems().get(0).getId());
        assertEquals(Long.valueOf(4), result.getItems().get(1).getId());
        assertEquals(Long.valueOf(1), result.getItems().get(2).getId());
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
        SearchResult materials = doGet(format(GET_BY_CREATOR_URL, username))
                .readEntity(SearchResult.class);

        assertEquals(0, materials.getItems().size());
        assertEquals(0, materials.getTotalResults());
        assertEquals(0, materials.getStart());

    }

    @Test
    public void create() {
        login("89012378912");

        Material material = new Material();
        material.setSource("http://www.whatisthis.example.ru");

        Subject subject = (Subject) taxonDAO.findTaxonById(22L);
        material.setTaxons(Arrays.asList(subject));

        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setId(1L);
        keyCompetence.setName("Cultural_and_value_competence");
        material.setKeyCompetences(Arrays.asList(keyCompetence));

        CrossCurricularTheme crossCurricularTheme = new CrossCurricularTheme();
        crossCurricularTheme.setId(2L);
        crossCurricularTheme.setName("Environment_and_sustainable_development");
        material.setCrossCurricularThemes(Arrays.asList(crossCurricularTheme));

        Response response = createMaterial(material);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Material createdMaterial = response.readEntity(Material.class);

        assertNotNull(createdMaterial.getKeyCompetences());
        assertEquals(1, createdMaterial.getKeyCompetences().size());
        KeyCompetence createdKeyCompetence = createdMaterial.getKeyCompetences().get(0);
        assertEquals(keyCompetence.getName(), createdKeyCompetence.getName());

        assertNotNull(createdMaterial.getCrossCurricularThemes());
        assertEquals(1, createdMaterial.getCrossCurricularThemes().size());
        CrossCurricularTheme createdCrossCurricularTheme = createdMaterial.getCrossCurricularThemes().get(0);
        assertEquals(crossCurricularTheme.getName(), createdCrossCurricularTheme.getName());
    }

    @Test
    public void createWithKeyCompetencesWhenNotAllowed() {
        login("89012378912");

        Material material = new Material();
        material.setSource("http://www.whatisthis.example.com");

        Subject subject = (Subject) taxonDAO.findTaxonById(21L);
        material.setTaxons(Arrays.asList(subject));

        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setId(1L);
        keyCompetence.setName("Cultural_and_value_competence");
        material.setKeyCompetences(Arrays.asList(keyCompetence));

        CrossCurricularTheme crossCurricularTheme = new CrossCurricularTheme();
        crossCurricularTheme.setId(2L);
        crossCurricularTheme.setName("Environment_and_sustainable_development");
        material.setCrossCurricularThemes(Arrays.asList(crossCurricularTheme));

        Response response = createMaterial(material);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Material createdMaterial = response.readEntity(Material.class);

        assertNull(createdMaterial.getKeyCompetences());
        assertNull(createdMaterial.getCrossCurricularThemes());
    }

    private Response createMaterial(Material material) {
        return doPut(CREATE_MATERIAL_URL, Entity.entity(material, MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void createOrUpdateAsRestrictedUser() {
        login("89898989890");

        Material material = new Material();
        material.setSource("http://example.com/restricted");

        Response response = createMaterial(material);
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void addRecommendation() {
        String idCode = "89898989898";
        User user = login(idCode);

        Material material = materialService.get(3L, user);

        Response response = doPost(MATERIAL_ADD_RECOMMENDATION,
                Entity.entity(material, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Recommendation recommendation = response.readEntity(Recommendation.class);
        assertNotNull(recommendation);
        assertEquals(Long.valueOf(8), recommendation.getCreator().getId());
    }

    @Test
    public void removeRecommendation() {
        String idCode = "89898989898";
        User user = login(idCode);

        Material material = materialService.get(3L, user);

        Response response = doPost(MATERIAL_REMOVE_RECOMMENDATION,
                Entity.entity(material, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void setBrokenMaterial() {
        login("89012378912");
        long materialId = 5;

        Material material = getMaterial(materialId);

        Response response = doPost(MATERIAL_SET_BROKEN, Entity.entity(material, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void setNotBroken() {
        login("89012378912");
        long materialId = 5;

        Material material = getMaterial(materialId);

        Response response = doPost(MATERIAL_SET_NOT_BROKEN, Entity.entity(material, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());

        login("89898989898");
        Response responseAdmin = doPost(MATERIAL_SET_NOT_BROKEN,
                Entity.entity(material, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.NO_CONTENT.getStatusCode(), responseAdmin.getStatus());

        Response hasBrokenResponse = doGet(MATERIAL_IS_BROKEN + "?materialId=" + material.getId(),
                MediaType.APPLICATION_JSON_TYPE);
        assertEquals(Status.OK.getStatusCode(), hasBrokenResponse.getStatus());

        assertEquals(hasBrokenResponse.readEntity(Boolean.class), false);
    }

    @Test
    public void hasSetBroken() {
        login("89012378912");
        long materialId = 5;

        Material material = getMaterial(materialId);

        Response response = doPost(MATERIAL_SET_BROKEN, Entity.entity(material, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Response hasBrokenResponse = doGet(MATERIAL_HAS_SET_BROKEN + "?materialId=" + material.getId(),
                MediaType.APPLICATION_JSON_TYPE);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        assertEquals(hasBrokenResponse.readEntity(Boolean.class), true);
    }

    @Test
    public void isBroken() {
        login("89012378912");
        long materialId = 5;

        Material material = getMaterial(materialId);

        Response response = doPost(MATERIAL_SET_BROKEN, Entity.entity(material, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Response isBrokenResponse = doGet(MATERIAL_IS_BROKEN + "?materialId=" + material.getId(),
                MediaType.APPLICATION_JSON_TYPE);
        assertEquals(Status.FORBIDDEN.getStatusCode(), isBrokenResponse.getStatus());

        login("89898989898");

        Response isBrokenResponseAdmin = doGet(MATERIAL_IS_BROKEN + "?materialId=" + material.getId(),
                MediaType.APPLICATION_JSON_TYPE);
        assertEquals(Status.OK.getStatusCode(), isBrokenResponseAdmin.getStatus());

        assertEquals(isBrokenResponseAdmin.readEntity(Boolean.class), true);
    }

    @Test
    public void getBroken() {
        login("89012378912");
        Response getBrokenResponse = doGet(MATERIAL_GET_BROKEN, MediaType.APPLICATION_JSON_TYPE);
        assertEquals(Status.FORBIDDEN.getStatusCode(), getBrokenResponse.getStatus());

        login("89898989898");

        long materialId = 5;
        Material material = getMaterial(materialId);
        Response response = doPost(MATERIAL_SET_BROKEN, Entity.entity(material, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Response getBrokenResponseAdmin = doGet(MATERIAL_GET_BROKEN, MediaType.APPLICATION_JSON_TYPE);
        List<BrokenContent> brokenMaterials = getBrokenResponseAdmin.readEntity(new GenericType<List<BrokenContent>>() {
        });
        boolean containsMaterial = false;
        for (BrokenContent brokenContent : brokenMaterials) {
            if (brokenContent.getMaterial().getId() == materialId) {
                containsMaterial = true;
            }
        }
        assertEquals(containsMaterial, true);
    }

    @Test(expected = RuntimeException.class)
    public void GetMaterialsByNullSource() {
        login("38011550077");

        Response response = doGet("material/getBySource?source=");

        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });
    }

    @Test
    public void GetMaterialsByNonExistantSource() {
        login("38011550077");

        Response response = doGet("material/getBySource?source=https://www.youtube.com/watch?v=5_Ar7VXXsro");

        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });

        assertEquals(0, materials.size());
    }

    @Test
    public void GetMaterialsBySource() {
        login("38011550077");

        Response response = doGet("material/getBySource?source=https://en.wikipedia.org/wiki/Power_Architecture");

        List<Material> materials = response.readEntity(new GenericType<List<Material>>() {
        });

        assertEquals(2, materials.size());
    }

    @Test
    public void deleteAndRestore() {
        login("89898989898");

        Long materialId = 13L;

        Material material = new Material();
        material.setId(materialId);

        Response response = doDelete("material/" + materialId);
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = doPost(RESTORE_MATERIAL, Entity.entity(material, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void userCanNotDeleteRepositoryMaterial() {
        login("38011550077");

        Long materialId = 12L;

        Response response = doDelete("material/" + materialId);
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void userCanNotRestoreRepositoryMaterial() {
        login("38011550077");

        Long materialId = 14L;

        Material material = new Material();
        material.setId(materialId);

        Response response = doPost(RESTORE_MATERIAL, Entity.entity(material, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
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
        assertEquals(new Long(1), material.getPicture().getId());
        assertEquals("picture1", material.getPicture().getName());
        assertNull(material.getPicture().getData());
        assertNotNull(material.getTaxons());
        assertEquals(2, material.getTaxons().size());
        assertEquals(new Long(2), material.getTaxons().get(0).getId());
        assertEquals(new Long(20), material.getTaxons().get(1).getId());
        assertNull(material.getRepository());
        assertNotNull(material.getRepositoryIdentifier());
        assertEquals(new Long(1), material.getCreator().getId());
        assertFalse(material.isEmbeddable());

        assertEquals(2, material.getTargetGroups().size());
        assertTrue(TargetGroupEnum.containsTargetGroup(material.getTargetGroups(), TargetGroupEnum.ZERO_FIVE));
        assertTrue(TargetGroupEnum.containsTargetGroup(material.getTargetGroups(), TargetGroupEnum.SIX_SEVEN));
        assertTrue(material.isSpecialEducation());
        assertEquals("Lifelong_learning_and_career_planning", material.getCrossCurricularThemes().get(0).getName());
        assertEquals("Cultural_and_value_competence", material.getKeyCompetences().get(0).getName());
    }

    private Material getMaterial(long materialId) {
        return doGet(format(GET_MATERIAL_URL, materialId), Material.class);
    }
}
