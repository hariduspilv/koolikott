package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.ChangedLearningObject;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.taxon.Taxon;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChangedLearningObjectAdminResourceTest extends ResourceIntegrationTestBase {

    private static final String GET_CHANGED_URL = "admin/changed/";
    private static final String GET_CHANGED_BY_ID_URL = "admin/changed/%s";
    private static final String GET_CHANGED_COUNT_URL = "admin/changed/count";
    private static final String ACCEPT_ALL_CHANGES_URL = "admin/changed/%s/acceptAll";
    private static final String REVERT_ALL_CHANGES_URL = "admin/changed/%s/revertAll";

    private static final String ADD_SYSTEM_TAG_URL = "learningObject/%s/system_tags?type=%s&name=%s";
    private static final String GET_TAXON_URL = "learningMaterialMetadata/taxon?taxonId=%s";
    private static final String UPDATE_MATERIAL_URL = "material";
    private static final String TYPE_MATERIAL = ".Material";
    private static final String TEST_SYSTEM_TAG = "mathematics";
    private static final long TEST_MATERIAL_ID = 5L;
    private static final long TEST_TAXON_ID = 10L;
    private static final int FALSE = 0;

    @Test
    public void getChanged_returns_changed_learningObjects() throws Exception {
        login(USER_ADMIN);
        changeMaterial(TEST_MATERIAL_ID);

        List<ChangedLearningObject> changedLearningObjects = doGet(GET_CHANGED_URL, new GenericType<List<ChangedLearningObject>>() {
        });
        long changedLearnigObjectsCount = doGet(GET_CHANGED_COUNT_URL, Long.class);

        assertTrue("LearningObjects are changed", changedLearningObjects.stream()
                .map(ChangedLearningObject::getLearningObject)
                .map(LearningObject::getChanged)
                .allMatch(integer -> integer > 0));
        assertEquals("Changed learningObject list size, changed learningObject count", changedLearningObjects.size(), changedLearnigObjectsCount);

        doGet(format(REVERT_ALL_CHANGES_URL, TEST_MATERIAL_ID));
    }

    @Test
    public void getChange_returns_changed_learningObjects_by_id() throws Exception {
        login(USER_ADMIN);
        changeMaterial(TEST_MATERIAL_ID);

        List<ChangedLearningObject> changedLearningObjectsById = doGet(format(GET_CHANGED_BY_ID_URL, TEST_MATERIAL_ID), new GenericType<List<ChangedLearningObject>>() {
        });

        assertTrue("LearningObject changed", changedLearningObjectsById.stream()
                .map(ChangedLearningObject::getLearningObject)
                .map(LearningObject::getChanged)
                .allMatch(integer -> integer > 0));
        assertTrue("Changed learningObject id", changedLearningObjectsById.stream()
                .map(ChangedLearningObject::getLearningObject)
                .allMatch(learningObject -> learningObject.getId().equals(TEST_MATERIAL_ID)));

        doGet(format(REVERT_ALL_CHANGES_URL, TEST_MATERIAL_ID));
    }

    @Ignore
    @Test
    public void revertAllChanges_removes_changes_from_learningObject() throws Exception {
        login(USER_ADMIN);
        changeMaterial(TEST_MATERIAL_ID);

        LearningObject revertedLearningObject = doGet(format(REVERT_ALL_CHANGES_URL, TEST_MATERIAL_ID), LearningObject.class);
        assertEquals("LearningObject not changed", FALSE, revertedLearningObject.getChanged());
    }

    @Test
    public void acceptAllChanges_removes_learningObject_from_ChangedLearningObject() throws Exception {
        login(USER_ADMIN);
        changeMaterial(TEST_MATERIAL_ID);

        doGet(format(ACCEPT_ALL_CHANGES_URL, TEST_MATERIAL_ID));

        List<ChangedLearningObject> changedLearningObjectsById = doGet(format(GET_CHANGED_BY_ID_URL, TEST_MATERIAL_ID), new GenericType<List<ChangedLearningObject>>() {
        });

        assertTrue(changedLearningObjectsById.isEmpty());

        doGet(format(REVERT_ALL_CHANGES_URL, TEST_MATERIAL_ID));
    }

    private void changeMaterial(Long materialId) {
        List<Taxon> taxons = new ArrayList<>();
        taxons.add(doGet(format(GET_TAXON_URL, TEST_TAXON_ID), Taxon.class));

        Material material = getMaterial(materialId);
        material.setTaxons(taxons);
        doPut(UPDATE_MATERIAL_URL, material);

        doGet(format(ADD_SYSTEM_TAG_URL, materialId, TYPE_MATERIAL, TEST_SYSTEM_TAG));
    }
}
