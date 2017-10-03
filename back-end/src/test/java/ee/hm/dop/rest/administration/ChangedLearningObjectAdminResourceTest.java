package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.model.ChangedLearningObject;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.taxon.Taxon;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChangedLearningObjectAdminResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_ALL_CHANGES = "admin/changed/";
    private static final String GET_CHANGES_BY_ID = "admin/changed/%s";
    private static final String GET_CHANGED_COUNT = "admin/changed/count";
    private static final String ACCEPT_ALL_CHANGES_URL = "admin/changed/%s/acceptAll";
    private static final String REVERT_ALL_CHANGES_URL = "admin/changed/%s/revertAll";

    private static final String ADD_SYSTEM_TAG_URL = "learningObject/%s/system_tags?type=%s&name=%s";
    private static final String GET_TAXON_URL = "learningMaterialMetadata/taxon?taxonId=%s";
    private static final String UPDATE_MATERIAL_URL = "material";
    private static final String TYPE_MATERIAL = ".Material";
    private static final String TEST_SYSTEM_TAG = "mathematics";
    private static final long TEST_UNREVIEWED_MATERIAL_ID = TestConstants.MATERIAL_9;
    private static final long TEST_TAXON_ForeignLanguage = 11L;
    private static final int FALSE = 0;

    @Test
    public void after_admin_changes_learningObject_they_can_find_it_by_asking_for_changes() throws Exception {
        login(TestConstants.USER_ADMIN);
        changeMaterial(TestConstants.MATERIAL_5);

        long changedLearnigObjectsCount = doGet(GET_CHANGED_COUNT, Long.class);

        List<ChangedLearningObject> changedLearningObjects = doGet(GET_ALL_CHANGES, list());
        assertTrue(CollectionUtils.isNotEmpty(changedLearningObjects));
        isChanged(changedLearningObjects);
        countEqual(changedLearnigObjectsCount, changedLearningObjects);

        List<ChangedLearningObject> changedLearningObjectsById = doGet(format(GET_CHANGES_BY_ID, TestConstants.MATERIAL_5), list());
        assertTrue(CollectionUtils.isNotEmpty(changedLearningObjectsById));
        isChanged(changedLearningObjectsById);
        idsEqual(changedLearningObjectsById, TestConstants.MATERIAL_5);

        doGet(format(REVERT_ALL_CHANGES_URL, TestConstants.MATERIAL_5));
    }

    @Test
    public void after_admin_changes_unReviewed_learningObject_no_changes_are_registered() throws Exception {
        login(TestConstants.USER_ADMIN);
        changeMaterial(TEST_UNREVIEWED_MATERIAL_ID);

        List<ChangedLearningObject> changedLearningObjectsById = doGet(format(GET_CHANGES_BY_ID, TEST_UNREVIEWED_MATERIAL_ID), list());

        assertTrue(CollectionUtils.isEmpty(changedLearningObjectsById));
        isChanged(changedLearningObjectsById);
        idsEqual(changedLearningObjectsById, TEST_UNREVIEWED_MATERIAL_ID);

        doGet(format(REVERT_ALL_CHANGES_URL, TEST_UNREVIEWED_MATERIAL_ID));
    }

    @Ignore
    @Test
    public void admin_can_revert_all_changes() throws Exception {
        login(TestConstants.USER_ADMIN);
        changeMaterial(TestConstants.MATERIAL_5);

        LearningObject revertedLearningObject = doGet(format(REVERT_ALL_CHANGES_URL, TestConstants.MATERIAL_5), LearningObject.class);
        assertEquals("LearningObject not changed", FALSE, revertedLearningObject.getChanged());
    }

    @Test
    public void admin_can_accept_all_changes() throws Exception {
        login(TestConstants.USER_ADMIN);
        changeMaterial(TestConstants.MATERIAL_5);
        doGet(format(ACCEPT_ALL_CHANGES_URL, TestConstants.MATERIAL_5));

        List<ChangedLearningObject> changedLearningObjectsById = doGet(format(GET_CHANGES_BY_ID, TestConstants.MATERIAL_5), list());
        assertTrue(changedLearningObjectsById.isEmpty());

        doGet(format(REVERT_ALL_CHANGES_URL, TestConstants.MATERIAL_5));
    }

    private void isChanged(List<ChangedLearningObject> changedLearningObjects) {
        assertTrue("LearningObjects are changed", changedLearningObjects.stream()
                .map(ChangedLearningObject::getLearningObject)
                .map(LearningObject::getChanged)
                .allMatch(integer -> integer > 0));
    }

    private void changeMaterial(Long materialId) {
        List<Taxon> taxons = Arrays.asList(doGet(format(GET_TAXON_URL, TEST_TAXON_ForeignLanguage), Taxon.class));

        Material material = getMaterial(materialId);
        material.setTaxons(taxons);
        doPut(UPDATE_MATERIAL_URL, material);

        doGet(format(ADD_SYSTEM_TAG_URL, materialId, TYPE_MATERIAL, TEST_SYSTEM_TAG));
    }

    private GenericType<List<ChangedLearningObject>> list() {
        return new GenericType<List<ChangedLearningObject>>() {
        };
    }

    private void countEqual(long changedLearnigObjectsCount, List<ChangedLearningObject> changedLearningObjects) {
        assertEquals("Changed learningObject list size, changed learningObject count", changedLearningObjects.size(), changedLearnigObjectsCount);
    }

    private void idsEqual(List<ChangedLearningObject> changedLearningObjectsById, long materialId) {
        assertTrue("Changed learningObject id", changedLearningObjectsById.stream()
                .map(ChangedLearningObject::getLearningObject)
                .allMatch(learningObject -> learningObject.getId().equals(materialId)));
    }
}
