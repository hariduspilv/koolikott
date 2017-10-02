package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.LanguageC;
import ee.hm.dop.model.enums.TargetGroupEnum;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.content.MaterialService;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class MaterialResourceTest extends ResourceIntegrationTestBase {

    private static final String MATERIAL_INCREASE_VIEW_COUNT_URL = "material/increaseViewCount";
    public static final String GET_MATERIAL_URL = "material?materialId=%s";
    private static final String GET_BY_CREATOR_URL = "material/getByCreator?username=%s";
    private static final String GET_BY_CREATOR_COUNT_URL = "material/getByCreator/count?username=%s";
    private static final String CREATE_MATERIAL_URL = "material";
    private static final String MATERIAL_SET_BROKEN = "material/setBroken";
    private static final String MATERIAL_GET_BROKEN = "admin/brokenContent/getBroken";
    private static final String MATERIAL_GET_BROKEN_COUNT = "admin/brokenContent/getBroken/count";
    private static final String MATERIAL_SET_NOT_BROKEN = "admin/brokenContent/setNotBroken";
    private static final String MATERIAL_HAS_SET_BROKEN = "material/hasSetBroken";
    private static final String MATERIAL_IS_BROKEN = "admin/brokenContent/isBroken";
    private static final String MATERIAL_ADD_RECOMMENDATION = "material/recommend";
    private static final String MATERIAL_REMOVE_RECOMMENDATION = "material/removeRecommendation";
    private static final String RESTORE_MATERIAL = "admin/deleted/material/restore";
    private static final String GET_DELETED_MATERIALS = "admin/deleted/material/getDeleted";
    private static final String GET_DELETED_MATERIALS_COUNT = "admin/deleted/material/getDeleted/count";
    private static final String LIKE_URL = "material/like";
    private static final String DISLIKE_URL = "material/dislike";
    private static final String GET_USER_LIKE_URL = "material/getUserLike";
    private static final String REMOVE_USER_LIKE_URL = "material/removeUserLike";
    private static final String EXTERNAL_MATERIAL_URL = "material/externalMaterial?url=%s";
    public static final String GET_MATERIAL_BY_SOURCE_URL = "material/getBySource?source=";
    public static final String GET_ONE_MATERIAL_BY_SOURCE_URL = "material/getOneBySource?source=";
    public static final String SOURCE_ONE_MATERIAL = "https://www.youtube.com/watch?v=gSWbx3CvVUk";
    public static final String SOURCE_NOT_EXISTING = "https://www.youtube.com/watch?v=5_Ar7VXXsro";
    public static final String SOURCE_MULTIPLE_MATERIALS = "https://en.wikipedia.org/wiki/Power_Architecture";

    @Inject
    private MaterialService materialService;

    @Inject
    private TaxonDao taxonDao;

    @Test
    public void getMaterial() {
        assertMaterial1(getMaterial(1));
    }

    @Test
    public void getMaterialDescriptionAndLanguage() {
        Material material = getMaterial(1);

        List<LanguageString> descriptions = material.getDescriptions();
        assertEquals(2, descriptions.size());
        for (LanguageString languageString : descriptions) {
            if (languageString.getId() == 1) {
                assertEquals(LanguageC.EST, languageString.getLanguage().getCode());
                assertEquals("Test description in estonian. (Russian available)", languageString.getText());
            } else if (languageString.getId() == 2) {
                assertEquals(LanguageC.EST, languageString.getLanguage().getCode());
                assertEquals("Test description in russian, which is the only language available.",
                        languageString.getText());

            }
        }
    }

    @Test
    public void getMaterialUpdatedDate() {
        Material material = getMaterial(2);
        assertEquals(new DateTime("1995-07-12T09:00:01.000+00:00"), material.getUpdated());
    }

    @Test
    public void increaseViewCount() {
        Material materialBefore = getMaterial(5L);

        Response response = doPost(MATERIAL_INCREASE_VIEW_COUNT_URL, materialWithId(5L));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Material materialAfter = getMaterial(5L);
        assertEquals(Long.valueOf(materialBefore.getViews() + 1), materialAfter.getViews());
    }

    @Test
    public void increaseViewCountNotExistingMaterial() {
        Response response = doGet(format(GET_MATERIAL_URL, 999L));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = doPost(MATERIAL_INCREASE_VIEW_COUNT_URL, materialWithId(999L));
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        response = doGet(format(GET_MATERIAL_URL, 999L));
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

        List<Long> collect = result.getItems().stream().map(Searchable::getId).collect(Collectors.toList());
        assertTrue(collect.containsAll(asList(8L, 4L, 1L)));
    }

    @Test
    public void getByCreatorCount_returns_same_materials_count_as_getByCreator_size() throws Exception {
        List<Searchable> materials = doGet(format(GET_BY_CREATOR_URL, "mati.maasikas")).readEntity(SearchResult.class).getItems();
        long count = doGet(format(GET_BY_CREATOR_COUNT_URL, "mati.maasikas"), Long.class);
        assertEquals("Materials size by creator, Materials count by creator", materials.size(), count);
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
        SearchResult materials = doGet(format(GET_BY_CREATOR_URL, username), SearchResult.class);

        assertEquals(0, materials.getItems().size());
        assertEquals(0, materials.getTotalResults());
        assertEquals(0, materials.getStart());
    }

    @Test
    public void create() {
        login(USER_SECOND);

        Material material = new Material();
        material.setSource("http://www.whatisthis.example.ru");

        Subject subject = (Subject) taxonDao.findById(22L);
        material.setTaxons(asList(subject));

        KeyCompetence keyCompetence = competence();
        material.setKeyCompetences(asList(keyCompetence));

        CrossCurricularTheme crossCurricularTheme = theme();
        material.setCrossCurricularThemes(asList(crossCurricularTheme));

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
        login(USER_SECOND);

        Material material = new Material();
        material.setSource("http://www.whatisthis.example.com");

        Subject subject = (Subject) taxonDao.findById(21L);
        material.setTaxons(asList(subject));

        material.setKeyCompetences(competenceList());

        material.setCrossCurricularThemes(themeList());

        Response response = createMaterial(material);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Material createdMaterial = response.readEntity(Material.class);

        assertNull(createdMaterial.getKeyCompetences());
        assertNull(createdMaterial.getCrossCurricularThemes());
    }

    @Test
    public void createOrUpdateAsRestrictedUser() {
        login(USER_RESTRICTED);

        Material material = new Material();
        material.setSource("http://example.com/restricted");

        Response response = createMaterial(material);
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void createOrUpdateMaterial_updates_existing_material() throws Exception {
        login(USER_PEETER);

        Material material = getMaterial(5L);
        material.setSpecialEducation(true);

        Material materialAfter = createMaterial(material).readEntity(Material.class);
        assertEquals("Material isSpecialEducation", material.isSpecialEducation(), materialAfter.isSpecialEducation());
    }

    @Test
    public void can_not_create_or_update_material_if_not_logged_in() throws Exception {
        Response response = createMaterial(new Material());
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void addRecommendation() {
        User user = login(USER_ADMIN);
        Material material = materialService.get(3L, user);

        Recommendation recommendation = doPost(MATERIAL_ADD_RECOMMENDATION, material, Recommendation.class);
        assertNotNull(recommendation);
        assertEquals(Long.valueOf(8), recommendation.getCreator().getId());
    }

    @Test
    public void removeRecommendation() {
        User user = login(USER_ADMIN);

        Material material = materialService.get(3L, user);
        Response response = doPost(MATERIAL_REMOVE_RECOMMENDATION, material);
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void setBrokenMaterial() {
        login(USER_SECOND);

        Material material = getMaterial(5L);
        Response response = doPost(MATERIAL_SET_BROKEN, material);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void setNotBroken() {
        login(USER_SECOND);

        Material material = getMaterial(5L);
        Response response = doPost(MATERIAL_SET_NOT_BROKEN, material);
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());

        login(USER_ADMIN);
        Response responseAdmin = doPost(MATERIAL_SET_NOT_BROKEN, material);
        assertEquals(Status.NO_CONTENT.getStatusCode(), responseAdmin.getStatus());

        Response hasBrokenResponse = doGet(MATERIAL_IS_BROKEN + "?materialId=" + material.getId());
        assertEquals(Status.OK.getStatusCode(), hasBrokenResponse.getStatus());
        assertEquals(hasBrokenResponse.readEntity(Boolean.class), false);
    }

    @Test
    public void hasSetBroken() {
        login(USER_SECOND);
        Material material = getMaterial(5L);

        Response response = doPost(MATERIAL_SET_BROKEN, material);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Response hasBrokenResponse = doGet(MATERIAL_HAS_SET_BROKEN + "?materialId=" + material.getId());
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertEquals(hasBrokenResponse.readEntity(Boolean.class), true);
    }

    @Test
    public void hasSetBroken_returns_false_if_user_is_not_logged_in() throws Exception {
        Boolean response = doGet(MATERIAL_HAS_SET_BROKEN + "?materialId=" + getMaterial(5L).getId(), Boolean.class);
        assertFalse("Material hasSetBroken", response);
    }

    @Test
    public void isBroken() {
        login(USER_SECOND);
        Material material = getMaterial(5L);

        Response response = doPost(MATERIAL_SET_BROKEN, material);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Response isBrokenResponse = doGet(MATERIAL_IS_BROKEN + "?materialId=" + material.getId());
        assertEquals(Status.FORBIDDEN.getStatusCode(), isBrokenResponse.getStatus());

        login(USER_ADMIN);

        Response isBrokenResponseAdmin = doGet(MATERIAL_IS_BROKEN + "?materialId=" + material.getId());
        assertEquals(Status.OK.getStatusCode(), isBrokenResponseAdmin.getStatus());

        assertEquals(isBrokenResponseAdmin.readEntity(Boolean.class), true);
    }

    @Test
    public void getBroken() {
        login(USER_SECOND);
        Response getBrokenResponse = doGet(MATERIAL_GET_BROKEN);
        assertEquals(Status.FORBIDDEN.getStatusCode(), getBrokenResponse.getStatus());

        login(USER_ADMIN);

        Material material = getMaterial(5L);
        Response response = doPost(MATERIAL_SET_BROKEN, material);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        Response getBrokenResponseAdmin = doGet(MATERIAL_GET_BROKEN, MediaType.APPLICATION_JSON_TYPE);
        List<BrokenContent> brokenMaterials = getBrokenResponseAdmin.readEntity(new GenericType<List<BrokenContent>>() {
        });
        boolean containsMaterial = false;
        for (BrokenContent brokenContent : brokenMaterials) {
            if (brokenContent.getMaterial().getId() == 5L) {
                containsMaterial = true;
            }
        }
        assertEquals(containsMaterial, true);

        long brokenMaterialsCount = doGet(MATERIAL_GET_BROKEN_COUNT, Long.class);
        assertEquals("Broken materials count", brokenMaterials.size(), brokenMaterialsCount);
    }

    @Test(expected = RuntimeException.class)
    public void GetMaterialsByNullSource() {
        login(USER_PEETER);
        doGet(GET_MATERIAL_BY_SOURCE_URL, listOfMaterials());
    }

    @Test
    public void GetMaterialsByNonExistantSource() {
        login(USER_PEETER);
        List<Material> materials = doGet(GET_MATERIAL_BY_SOURCE_URL + SOURCE_NOT_EXISTING, listOfMaterials());
        assertEquals(0, materials.size());
    }

    @Test
    public void GetMaterialsBySource() {
        login(USER_PEETER);
        List<Material> materials = doGet(GET_MATERIAL_BY_SOURCE_URL + SOURCE_MULTIPLE_MATERIALS, listOfMaterials());
        assertEquals(2, materials.size());
    }

    @Test
    public void getOneBySource_returns_one_material_by_source() throws Exception {
        login(USER_PEETER);
        Material materialBySource = doGet(GET_ONE_MATERIAL_BY_SOURCE_URL + SOURCE_ONE_MATERIAL, Material.class);
        assertNotNull(materialBySource);
        assertEquals("Material source", SOURCE_ONE_MATERIAL, materialBySource.getSource());
    }

    @Test
    public void deleteAndRestore() {
        login(USER_ADMIN);

        Response response = doDelete("material/" + 13L);
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

        Response response2 = doPost(RESTORE_MATERIAL, materialWithId(13L));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response2.getStatus());
    }

    @Test
    public void userCanNotDeleteRepositoryMaterial() {
        login(USER_PEETER);
        Response response = doDelete("material/" + 12L);
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void userCanNotRestoreRepositoryMaterial() {
        login(USER_PEETER);
        Response response = doPost(RESTORE_MATERIAL, materialWithId(14L));
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void getDeleted_returns_deleted_materials_to_user_admin() throws Exception {
        login(USER_ADMIN);
        List<Material> deletedMaterials = doGet(GET_DELETED_MATERIALS, new GenericType<List<Material>>() {
        });
        long deletedMaterialsCount = doGet(GET_DELETED_MATERIALS_COUNT, Long.class);

        assertTrue("Materials are deleted", deletedMaterials.stream().allMatch(LearningObject::isDeleted));
        assertEquals("Deleted materials list size, deleted materials count", deletedMaterials.size(), deletedMaterialsCount);
    }

    @Test
    public void regular_user_do_not_have_access_to_get_deleted_materials() throws Exception {
        login(USER_PEETER);
        Response response = doGet(GET_DELETED_MATERIALS);
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void likeMaterial_sets_it_as_liked() throws Exception {
        login(USER_PEETER);
        Material material = getMaterial(5L);

        doPost(LIKE_URL, material);
        UserLike userLike = doPost(GET_USER_LIKE_URL, material, UserLike.class);
        assertNotNull("User like exist", userLike);
        assertEquals("Material is liked by user", true, userLike.isLiked());
    }

    @Test
    public void dislikeMaterial_sets_it_as_not_liked() throws Exception {
        login(USER_PEETER);
        Material material = getMaterial(5L);

        doPost(DISLIKE_URL, material);
        UserLike userDislike = doPost(GET_USER_LIKE_URL, material, UserLike.class);
        assertNotNull("User dislike exist", userDislike);
        assertEquals("Material is disliked by user", false, userDislike.isLiked());
    }

    @Test
    public void removeUserLike_removes_like_from_material() throws Exception {
        login(USER_PEETER);
        Material material = getMaterial(5L);

        doPost(LIKE_URL, material);
        doPost(REMOVE_USER_LIKE_URL, material);
        UserLike userRemoveLike = doPost(GET_USER_LIKE_URL, material, UserLike.class);
        assertNull("User removed like does not exist", userRemoveLike);
    }

    @Test
    public void getProxyUrl_returns_external_material_if_it_exists() throws Exception {
        Response response = doGet(format(EXTERNAL_MATERIAL_URL, getMaterial(3L).getSource()), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull("Response input stream", response.readEntity(InputStream.class).read());
    }

    private void assertMaterial1(Material material) {
        assertEquals(2, material.getTitles().size());
        assertEquals("Matemaatika õpik üheksandale klassile", material.getTitles().get(0).getText());
        assertEquals(2, material.getDescriptions().size());
        assertEquals("Test description in estonian. (Russian available)", material.getDescriptions().get(0).getText());
        Language descriptionLanguage = material.getDescriptions().get(0).getLanguage();
        assertEquals(LanguageC.EST, descriptionLanguage.getCode());
        assertNotNull(descriptionLanguage.getName());
        assertNotNull(descriptionLanguage.getCodes());
        Language language = material.getLanguage();
        assertNotNull(language);
        assertEquals(LanguageC.EST, language.getCode());
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

        assertEquals("CCBY", material.getLicenseType().getName());
        assertEquals("Koolibri", material.getPublishers().get(0).getName());
        assertEquals(new DateTime("1999-01-01T02:00:01.000+02:00"), material.getAdded());

        assertEquals(5, material.getTags().size());
        assertEquals("matemaatika", material.getTags().get(0).getName());
        assertEquals("põhikool", material.getTags().get(1).getName());
        assertEquals("õpik", material.getTags().get(2).getName());
        assertEquals("mathematics", material.getTags().get(3).getName());
        assertEquals("book", material.getTags().get(4).getName());
    }

    private Response createMaterial(Material material) {
        return doPut(CREATE_MATERIAL_URL, material);
    }

    private List<CrossCurricularTheme> themeList() {
        return asList(theme());
    }

    private List<KeyCompetence> competenceList() {
        return asList(competence());
    }

    private CrossCurricularTheme theme() {
        CrossCurricularTheme crossCurricularTheme = new CrossCurricularTheme();
        crossCurricularTheme.setId(2L);
        crossCurricularTheme.setName("Environment_and_sustainable_development");
        return crossCurricularTheme;
    }

    private KeyCompetence competence() {
        KeyCompetence keyCompetence = new KeyCompetence();
        keyCompetence.setId(1L);
        keyCompetence.setName("Cultural_and_value_competence");
        return keyCompetence;
    }

    private GenericType<List<Material>> listOfMaterials() {
        return new GenericType<List<Material>>() {
        };
    }
}
