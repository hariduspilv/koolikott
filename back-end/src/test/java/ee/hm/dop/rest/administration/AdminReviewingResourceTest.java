package ee.hm.dop.rest.administration;

import com.google.inject.Inject;
import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.dao.TestDao;
import ee.hm.dop.model.Material;
import ee.hm.dop.utils.DbUtils;
import org.junit.Test;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class AdminReviewingResourceTest extends ResourceIntegrationTestBase {

    private static final String MATERIAL_SET_NOT_BROKEN = "admin/brokenContent/setNotBroken";
    private static final String MATERIAL_SET_NOT_IMPROPER = "impropers?learningObject=%s";
    private static final String MATERIAL_DELETE = "material/";
    private static final String MATERIAL_RESTORE = "admin/deleted/material/restore";

    @Inject
    private TestDao testDao;

    @Test
    public void approving_improper_content_approves_everything() throws Exception {
        login(USER_ADMIN);

        assertHasWorkToDo(getMaterial(MATERIAL_15));
        doDelete(format(MATERIAL_SET_NOT_IMPROPER, MATERIAL_15));
        assertWorkIsDone(getMaterial(MATERIAL_15));

        restoreLearningObjectChanges(MATERIAL_15);
    }

    @Test
    public void approving_broken_content_approves_everything() throws Exception {
        login(USER_ADMIN);

        assertHasWorkToDo(getMaterial(MATERIAL_15));
        doPost(MATERIAL_SET_NOT_BROKEN, getMaterial(MATERIAL_15));
        assertWorkIsDone(getMaterial(MATERIAL_15));

        restoreLearningObjectChanges(MATERIAL_15);
    }

    @Test
    public void deleting_material_approves_everything() throws Exception {
        login(USER_ADMIN);

        assertHasWorkToDo(getMaterial(MATERIAL_15));
        doDelete(MATERIAL_DELETE + MATERIAL_15);
        assertWorkIsDone(getMaterial(MATERIAL_15), true);

        restoreLearningObjectChanges(MATERIAL_15);
    }

    @Test
    public void restoring_material_approves_everything() throws Exception {
        login(USER_ADMIN);

        assertHasWorkToDo(getMaterial(MATERIAL_15));
        doDelete(MATERIAL_DELETE + MATERIAL_15);
        doPost(MATERIAL_RESTORE, getMaterial(MATERIAL_15));
        assertWorkIsDone(getMaterial(MATERIAL_15));

        restoreLearningObjectChanges(MATERIAL_15);
    }

    private void assertHasWorkToDo(Material material) {
        assertTrue(material.getBroken() > 0);
        assertTrue(material.getImproper() > 0);
        assertTrue(material.getUnReviewed() > 0);
        assertFalse(material.isDeleted());
    }

    private void assertWorkIsDone(Material materialAfter, boolean deleted) {
        assertTrue(materialAfter.getBroken() == 0);
        assertTrue(materialAfter.getImproper() == 0);
        assertTrue(materialAfter.getUnReviewed() == 0);
        assertEquals(deleted, materialAfter.isDeleted());
    }

    private void assertWorkIsDone(Material materialAfter) {
        assertWorkIsDone(materialAfter, false);
    }

    private void restoreLearningObjectChanges(Long learningObjectId) {
        DbUtils.getTransaction().begin();
        testDao.restore(learningObjectId);
        DbUtils.closeTransaction();
    }
}
