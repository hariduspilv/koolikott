package ee.hm.dop.rest.content;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestLayer;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.KeyCompetence;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.enums.LanguageC;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import java.time.LocalDateTime;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static ee.hm.dop.model.taxon.TaxonLevel.DOMAIN;
import static ee.hm.dop.model.taxon.TaxonLevel.EDUCATIONAL_CONTEXT;
import static ee.hm.dop.model.taxon.TaxonLevel.MODULE;
import static ee.hm.dop.model.taxon.TaxonLevel.SPECIALIZATION;
import static ee.hm.dop.model.taxon.TaxonLevel.SUBJECT;
import static ee.hm.dop.model.taxon.TaxonLevel.SUBTOPIC;
import static ee.hm.dop.model.taxon.TaxonLevel.TOPIC;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Transactional
public class MaterialResourceTest extends ResourceIntegrationTestBase {

    public static final String GET_MATERIAL_URL = "material?id=%s";
    public static final String GET_BY_CREATOR_URL = "material/getByCreator?username=%s";
    public static final String GET_BY_CREATOR_COUNT_URL = "material/getByCreator/count?username=%s";
    public static final String CREATE_MATERIAL_URL = "material/create";
    public static final String UPDATE_MATERIAL_URL = "material/update";
    public static final String RESTORE_MATERIAL = "admin/deleted/restore";
    public static final String EXTERNAL_MATERIAL_URL = "material/externalMaterial?id=%s&url=%s";
    public static final String GET_MATERIAL_BY_SOURCE_URL = "material/getBySource?source=";
    public static final String GET_ONE_MATERIAL_BY_SOURCE_URL = "material/getOneBySource?source=";
    public static final String SOURCE_ONE_MATERIAL = "https://www.youtube.com/watch?v=gSWbx3CvVUk";
    public static final String SOURCE_NOT_EXISTING = "https://www.youtube.com/watch?v=5_Ar7VXXsro";
    public static final String SOURCE_MULTIPLE_MATERIALS = "https://en.wikipedia.org/wiki/Power_Architecture";
    public static final String MATERIAL_DELETE = "material/delete";

    @Inject
    private TaxonDao taxonDao;

    @Test
    public void getMaterial() {
        assertMaterial1(getMaterial(MATERIAL_1), TestLayer.REST);
    }

    @Test
    public void getMaterialDescriptionAndLanguage() {
        Material material = getMaterial(MATERIAL_1);

        List<LanguageString> descriptions = material.getDescriptions();
        assertEquals(2, descriptions.size());
        for (LanguageString languageString : descriptions) {
            if (languageString.getId() == 1) {
                assertEquals(LanguageC.EST, languageString.getLanguage().getCode());
                assertEquals("Test description in estonian. (Russian available)", languageString.getText());
            } else if (languageString.getId() == 2) {
                assertEquals(LanguageC.EST, languageString.getLanguage().getCode());
                assertEquals("Test description in russian, which is the only language available.", languageString.getText());
            }
        }
    }

    @Test
    public void getMaterialUpdatedDate() {
        //assertEquals(LocalDateTime.parse("1995-07-12T09:00:01.000+00:00"), getMaterial(MATERIAL_2).getUpdated());
        //todo time
    }

    @Test
    public void getMaterialWithSubjects() {
        Material material = getMaterial(MATERIAL_6);

        List<Taxon> taxons = material.getTaxons();
        assertNotNull(taxons);
        assertEquals(2, taxons.size());
        Subject biology = (Subject) taxons.get(0);
        assertEquals(Long.valueOf(20), biology.getId());
        assertEquals("Biology", biology.getName());
        Subject math = (Subject) taxons.get(1);
        assertEquals(Long.valueOf(21), math.getId());
        assertEquals("Mathematics", math.getName());
    }

    @Test
    public void getMaterialWithNoTaxon() {
        Material material = getMaterial(MATERIAL_8);
        List<Taxon> taxons = material.getTaxons();
        assertNotNull(taxons);
        assertEquals(0, taxons.size());
    }

    @Test
    public void everybody_can_ask_for_mati_materials() {
        SearchResult result = doGet(format(GET_BY_CREATOR_URL, USER_MATI.username), SearchResult.class);
        List<Long> collect = result.getItems().stream().map(Searchable::getId).collect(Collectors.toList());
        assertTrue(collect.containsAll(asList(MATERIAL_8, MATERIAL_4, MATERIAL_1)));
    }

    @Test
    public void everybody_can_ask_for_taxon_user_materials() {
        SearchResult result = doGet(format(GET_BY_CREATOR_URL, USER_TAXON_USER.username), SearchResult.class);
        List<Long> collect = result.getItems().stream().map(Searchable::getId).collect(Collectors.toList());
        assertTrue(collect.containsAll(asList(MATERIAL_40, MATERIAL_41, MATERIAL_42)));
    }

    @Test
    public void getByCreatorCount_returns_same_materials_count_as_getByCreator_size() throws Exception {
        List<Searchable> materials = doGet(format(GET_BY_CREATOR_URL, USER_MATI.username)).readEntity(SearchResult.class).getItems();
        long count = doGet(format(GET_BY_CREATOR_COUNT_URL, USER_MATI.username), Long.class);
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
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void getByCreatorNoMaterials() {
        SearchResult materials = doGet(format(GET_BY_CREATOR_URL, USER_VOLDERMAR.username), SearchResult.class);

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

        Material material = getMaterial(MATERIAL_5);
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
    public void getOneBySource_returns_one_material_by_source() throws Exception {
        login(USER_PEETER);
        Material materialBySource = doGet(GET_ONE_MATERIAL_BY_SOURCE_URL + SOURCE_ONE_MATERIAL, Material.class);
        assertNotNull(materialBySource);
        assertEquals("Material source", SOURCE_ONE_MATERIAL, materialBySource.getSource());
    }

    @Test
    public void asking_materials_by_null_source_is_a_bad_request() {
        login(USER_PEETER);
        Response response = doGet(GET_MATERIAL_BY_SOURCE_URL);
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void asking_materials_by_source_present_in_multiple_materials_returns_multiple_materials() {
        login(USER_PEETER);
        List<Material> materials = doGet(GET_MATERIAL_BY_SOURCE_URL + SOURCE_MULTIPLE_MATERIALS, listOfMaterials());
        assertEquals(2, materials.size());
    }

    @Test
    public void asking_materials_by_nonexistant_source_returns_empty_list() {
        login(USER_PEETER);
        List<Material> materials = doGet(GET_MATERIAL_BY_SOURCE_URL + SOURCE_NOT_EXISTING, listOfMaterials());
        assertEquals(0, materials.size());
    }

    @Test
    public void userCanNotDeleteRepositoryMaterial_only_admin_can() {
        login(USER_PEETER);
        Response response = doPost(MATERIAL_DELETE, materialWithId(MATERIAL_12));
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void userCanNotRestoreRepositoryMaterial_only_admin_can() {
        login(USER_PEETER);
        Response response = doPost(RESTORE_MATERIAL, materialWithId(MATERIAL_14));
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void getProxyUrl_returns_external_material_if_it_exists() throws Exception {
        Response response = doGet(format(EXTERNAL_MATERIAL_URL, MATERIAL_3, getMaterial(MATERIAL_3).getSource()), MediaType.APPLICATION_OCTET_STREAM_TYPE);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        int available = response.readEntity(InputStream.class).available();
        assertTrue("Response input stream", available > 0);
    }

    @Test
    public void get_material_with_taxons_EC_DOMAIN_TOPIC() {
        login(USER_ADMIN);
        Material material = getMaterial(MATERIAL_40);

        validate_EC_DOMAIN_TOPIC(material);
        Material updatedMaterial = doPost(UPDATE_MATERIAL_URL, material, Material.class);
        validate_EC_DOMAIN_TOPIC(updatedMaterial);
    }

    @Test
    public void get_material_with_taxons_EC_DOMAIN_SUBJECT_TOPIC_SUBTOPIC() {
        login(USER_ADMIN);
        Material material = getMaterial(MATERIAL_41);

        validate_EC_DOMAIN_SUBJECT_TOPIC_SUBTOPIC(material);
        Material updatedMaterial = doPost(UPDATE_MATERIAL_URL, material, Material.class);
        validate_EC_DOMAIN_SUBJECT_TOPIC_SUBTOPIC(updatedMaterial);
    }

    @Test
    public void get_material_with_taxons_EC_DOMAIN_SPECIALIZATION_MODULE_TOPIC_SUBTOPIC() {
        login(USER_ADMIN);
        Material material = getMaterial(MATERIAL_42);

        validate_ALL_THE_TAXONLEVELS(material);
        Material updatedMaterial = doPost(UPDATE_MATERIAL_URL, material, Material.class);
        validate_ALL_THE_TAXONLEVELS(updatedMaterial);
    }

    private Response createMaterial(Material material) {
        if (material.getId() == null) {
            return doPost(CREATE_MATERIAL_URL, material);
        }
        return doPost(UPDATE_MATERIAL_URL, material);
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

    public void validate_EC_DOMAIN_TOPIC(Material material) {
        List<Taxon> taxons = material.getTaxons();
        assertNotNull(taxons);
        assertEquals(3, taxons.size());
        assertEquals(EDUCATIONAL_CONTEXT, taxons.get(0).getTaxonLevel());
        assertEquals(DOMAIN, taxons.get(1).getTaxonLevel());
        assertEquals(TOPIC, taxons.get(2).getTaxonLevel());
    }

    public void validate_EC_DOMAIN_SUBJECT_TOPIC_SUBTOPIC(Material material) {
        List<Taxon> taxons = material.getTaxons();
        assertEquals(5, taxons.size());
        assertEquals(EDUCATIONAL_CONTEXT, taxons.get(0).getTaxonLevel());
        assertEquals(DOMAIN, taxons.get(1).getTaxonLevel());
        assertEquals(SUBJECT, taxons.get(2).getTaxonLevel());
        assertEquals(TOPIC,taxons.get(3).getTaxonLevel());
        assertEquals(SUBTOPIC,taxons.get(4).getTaxonLevel());
    }

    public void validate_ALL_THE_TAXONLEVELS(Material material) {
        int i = 0;
        List<Taxon> taxons = material.getTaxons();
        assertEquals(6, taxons.size());
        assertEquals(EDUCATIONAL_CONTEXT, taxons.get(i++).getTaxonLevel());
        assertEquals(DOMAIN, taxons.get(i++).getTaxonLevel());
        assertEquals(TOPIC, taxons.get(i++).getTaxonLevel());
        assertEquals(SPECIALIZATION,taxons.get(i++).getTaxonLevel());
        assertEquals(MODULE,taxons.get(i++).getTaxonLevel());
        assertEquals(SUBTOPIC,taxons.get(i++).getTaxonLevel());
    }
}
