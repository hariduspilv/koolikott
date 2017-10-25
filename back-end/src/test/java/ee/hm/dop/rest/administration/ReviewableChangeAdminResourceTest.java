package ee.hm.dop.rest.administration;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.dao.TestDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReportingReasonEnum;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.utils.DbUtils;
import org.apache.commons.collections.CollectionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.*;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReviewableChangeAdminResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_ALL_CHANGES = "admin/changed/";
    private static final String GET_CHANGES_BY_ID = "admin/changed/%s";
    private static final String GET_CHANGED_COUNT = "admin/changed/count";
    private static final String ACCEPT_ALL_CHANGES_URL = "admin/changed/%s/acceptAll";
    private static final String REVERT_ALL_CHANGES_URL = "admin/changed/%s/revertAll";

    private static final String ADD_SYSTEM_TAG_URL = "learningObject/%s/system_tags";
    private static final String GET_TAXON_URL = "learningMaterialMetadata/taxon?taxonId=%s";
    public static final String UPDATE_MATERIAL_URL = "material";
    private static final String TYPE_MATERIAL = ".Material";
    private static final String TEST_SYSTEM_TAG = "mathematics";
    private static final long TEST_UNREVIEWED_MATERIAL_ID = MATERIAL_16;
    private static final long TEST_TAXON_ForeignLanguage = 11L;
    private static final int FALSE = 0;
    public static final String M_9_ORIGINAL_SOURCE = "http://www.chaging.it.com";
    public static final String SET_IMPROPER = "impropers";
    public static final String SET_BROKEN = "material/setBroken";

    @Inject
    private TestDao testDao;

    @Before
    public void setUp() throws Exception {
        login(USER_ADMIN);
    }

    @After
    public void tearDown() throws Exception {
        restoreLearningObjectChanges(Arrays.asList(MATERIAL_16));
    }

    @Test
    public void changes_are_registered_on_adding_new_system_tag() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertDoesntHave(material, TAXON_MATHEMATICS_DOMAIN);
        doPut(format(ADD_SYSTEM_TAG_URL, MATERIAL_16), tag(TAXON_MATHEMATICS_DOMAIN.name));
        Material updatedMaterial = getMaterial(MATERIAL_16);
        assertHas(updatedMaterial, TAXON_MATHEMATICS_DOMAIN);
    }

    @Test
    public void changes_are_not_registered_on_removing_an_existing_system_tag() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertDoesntHave(material, TAXON_FOREIGNLANGUAGE_DOMAIN);
        doPut(format(ADD_SYSTEM_TAG_URL, MATERIAL_16), tag(TAXON_FOREIGNLANGUAGE_DOMAIN.name));
        Material updatedMaterial = getMaterial(MATERIAL_16);
        assertHas(updatedMaterial, TAXON_FOREIGNLANGUAGE_DOMAIN);
        updatedMaterial.setTags(new ArrayList<>());
        Material updatedMaterial2 = createOrUpdateMaterial(updatedMaterial);
        assertHasTaxonNotTag(updatedMaterial2, TAXON_FOREIGNLANGUAGE_DOMAIN);
    }

    @Test
    public void changes_are_registered_on_new_source() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertNotChanged(material, M_9_ORIGINAL_SOURCE);
        String newSource = "http://www.bujakaa.ee";
        material.setSource(newSource);
        Material updateMaterial = createOrUpdateMaterial(material);
        assertChanged(updateMaterial, newSource);
    }

    
    @Test
    public void changes_are_not_registered_on_adding_same_source() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertNotChanged(material, M_9_ORIGINAL_SOURCE);
        material.setSource(M_9_ORIGINAL_SOURCE);
        Material updateMaterial = createOrUpdateMaterial(material);
        assertNotChanged(updateMaterial, M_9_ORIGINAL_SOURCE);
    }

    @Test
    public void changes_are_not_registered_when_LO_is_improper() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertDoesntHave(material);
        doPut(SET_IMPROPER, improper(material));
        Material updatedMaterial = getMaterial(MATERIAL_16);
        assertHas(updatedMaterial, ReviewType.IMPROPER);
    }

    @Test
    public void changes_are_not_registered_when_LO_is_broken() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertDoesntHave(material);
        doPost(SET_BROKEN, material);
        Material updatedMaterial = getMaterial(MATERIAL_16);
        assertHas(updatedMaterial, ReviewType.BROKEN);
    }

    @Test
    public void changes_are_not_registered_when_LO_is_unreviewed() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertDoesntHave(material);
        setUnreviewed(Lists.newArrayList(MATERIAL_16));
        Material updatedMaterial = getMaterial(MATERIAL_16);
        assertHas(updatedMaterial, ReviewType.FIRST);
    }

    @Ignore
    @Test
    public void I_add_new_system_tag_then_update_material_not_to_have_it___change_is_removed() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertDoesntHave(material, TAXON_MATHEMATICS_DOMAIN);
        doPut(format(ADD_SYSTEM_TAG_URL, MATERIAL_16), tag(TAXON_MATHEMATICS_DOMAIN.name));
        Material updatedMaterial = getMaterial(MATERIAL_16);
        assertHas(updatedMaterial, TAXON_MATHEMATICS_DOMAIN);

        updatedMaterial.setTaxons(new ArrayList<>());
        Material updatedMaterial2 = createOrUpdateMaterial(updatedMaterial);
        assertHasTagNotTaxon(updatedMaterial2, TAXON_MATHEMATICS_DOMAIN);
    }

    @Test
    public void I_add_new_system_tag_it_is_approved_then_I_update_material_not_to_have_it___change_is_reviewed_not_removed() throws Exception {

    }

    @Test
    public void I_change_bieber_url_to_beyonce___material_has_beyonce_url_change_has_bieber() throws Exception {

    }

    @Test
    public void I_change_bieber_url_to_beyonce_then_to_madonna___material_has_madonna_url_change_has_bieber() throws Exception {

    }

    @Test
    public void I_change_bieber_url_to_beyonce_it_is_reviewed_then_I_change_it_to_madonna___material_has_madonna_url_1change_is_reviewed_with_beyonce_1change_unreviewed_with_madonna
            () throws Exception {

    }

    @Deprecated
    @Ignore("flaky test")
    @Test
    public void after_admin_changes_learningObject_they_can_find_it_by_asking_for_changes() throws Exception {
        login(USER_ADMIN);
        changeMaterial(MATERIAL_5);

        long changedLearnigObjectsCount = doGet(GET_CHANGED_COUNT, Long.class);

        List<ReviewableChange> reviewableChanges = doGet(GET_ALL_CHANGES, list());
        assertTrue(CollectionUtils.isNotEmpty(reviewableChanges));
        isChanged(reviewableChanges);
        countEqual(changedLearnigObjectsCount, reviewableChanges);

        List<ReviewableChange> changedLearningObjectsById = doGet(format(GET_CHANGES_BY_ID, MATERIAL_5), list());
        assertTrue(CollectionUtils.isNotEmpty(changedLearningObjectsById));
        isChanged(changedLearningObjectsById);
        idsEqual(changedLearningObjectsById, MATERIAL_5);

        doGet(format(REVERT_ALL_CHANGES_URL, MATERIAL_5));
    }

    @Deprecated
    @Ignore
    @Test
    public void after_admin_changes_unReviewed_learningObject_no_changes_are_registered() throws Exception {
        login(USER_ADMIN);
        changeMaterial(TEST_UNREVIEWED_MATERIAL_ID);

        List<ReviewableChange> changedLearningObjectsById = doGet(format(GET_CHANGES_BY_ID, TEST_UNREVIEWED_MATERIAL_ID), list());

        assertTrue(CollectionUtils.isEmpty(changedLearningObjectsById));
        isChanged(changedLearningObjectsById);
        idsEqual(changedLearningObjectsById, TEST_UNREVIEWED_MATERIAL_ID);

        doGet(format(REVERT_ALL_CHANGES_URL, TEST_UNREVIEWED_MATERIAL_ID));
    }

    @Deprecated
    @Ignore
    @Test
    public void admin_can_revert_all_changes() throws Exception {
        login(USER_ADMIN);
        changeMaterial(MATERIAL_5);

        Response response = doPost(format(REVERT_ALL_CHANGES_URL, MATERIAL_5));
        LearningObject revertedLearningObject = response.readEntity(LearningObject.class);
        assertEquals("LearningObject not changed", FALSE, revertedLearningObject.getChanged());
    }

    @Deprecated
    @Ignore
    @Test
    public void admin_can_accept_all_changes() throws Exception {
        login(USER_ADMIN);
        changeMaterial(MATERIAL_5);
        doPost(format(ACCEPT_ALL_CHANGES_URL, MATERIAL_5));

        List<ReviewableChange> changedLearningObjectsById = doGet(format(GET_CHANGES_BY_ID, MATERIAL_5), list());
        assertTrue(changedLearningObjectsById.isEmpty());

        doPost(format(REVERT_ALL_CHANGES_URL, MATERIAL_5));
    }

    @Test
    public void admin_can_revert_one_change() throws Exception {
    }

    @Test
    public void admin_can_accept_one_() throws Exception {
    }

    private void isChanged(List<ReviewableChange> reviewableChanges) {
        assertTrue("LearningObjects are changed", reviewableChanges.stream()
                .map(ReviewableChange::getLearningObject)
                .map(LearningObject::getChanged)
                .allMatch(integer -> integer > 0));
    }

    private void changeMaterial(Long materialId) {
        List<Taxon> taxons = Arrays.asList(doGet(format(GET_TAXON_URL, TEST_TAXON_ForeignLanguage), Taxon.class));

        Material material = getMaterial(materialId);
        material.setTaxons(taxons);
        createOrUpdateMaterial(material);

        doPost(format(ADD_SYSTEM_TAG_URL, materialId, TYPE_MATERIAL, TEST_SYSTEM_TAG));
    }

    private GenericType<List<ReviewableChange>> list() {
        return new GenericType<List<ReviewableChange>>() {
        };
    }

    private void countEqual(long changedLearnigObjectsCount, List<ReviewableChange> reviewableChanges) {
        assertEquals("Changed learningObject list size, changed learningObject count", reviewableChanges.size(), changedLearnigObjectsCount);
    }

    private void idsEqual(List<ReviewableChange> changedLearningObjectsById, long materialId) {
        assertTrue("Changed learningObject id", changedLearningObjectsById.stream()
                .map(ReviewableChange::getLearningObject)
                .allMatch(learningObject -> learningObject.getId().equals(materialId)));
    }

    private void restoreLearningObjectChanges(List<Long> learningObjectId) {
        DbUtils.getTransaction().begin();
        testDao.removeChanges(learningObjectId);
        DbUtils.closeTransaction();
    }

    private void setUnreviewed(List<Long> learningObjectId) {
        DbUtils.getTransaction().begin();
        testDao.setUnReviewed(learningObjectId);
        DbUtils.closeTransaction();
    }

    public static ImproperContent improper(Material material) {
        ImproperContent json = new ImproperContent();
        ReportingReason reason = new ReportingReason();
        reason.setReason(ReportingReasonEnum.LO_CONTENT);
        json.setLearningObject(material);
        json.setReportingReasons(Lists.newArrayList(reason));
        return json;
    }
}
