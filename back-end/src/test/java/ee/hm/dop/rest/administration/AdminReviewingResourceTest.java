package ee.hm.dop.rest.administration;

import com.google.inject.Inject;
import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.dao.TestDao;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.interfaces.IMaterial;
import ee.hm.dop.utils.DbUtils;
import org.junit.Test;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class AdminReviewingResourceTest extends ResourceIntegrationTestBase {

    private static final String MATERIAL_SET_NOT_BROKEN = "admin/brokenContent/setNotBroken";
    private static final String MATERIAL_SET_NOT_IMPROPER = "admin/improper/setProper?learningObject=%s";
    private static final String MATERIAL_DELETE = "material/";
    private static final String MATERIAL_RESTORE = "admin/deleted/material/restore";
    private static final String PORTFOLIO_DELETE = "portfolio/delete";
    private static final String PORTFOLIO_RESTORE = "admin/deleted/portfolio/restore";

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

    @Test
    public void deleting_portfolio_approves_everything() throws Exception {
        login(USER_ADMIN);

        assertHasWorkToDo(getPortfolio(PORTFOLIO_15));
        doPost(PORTFOLIO_DELETE, portfolioWithId(PORTFOLIO_15));
        assertWorkIsDone(getPortfolio(PORTFOLIO_15), true);

        restoreLearningObjectChanges(PORTFOLIO_15);
    }

    @Test
    public void restoring_portfolio_approves_everything() throws Exception {
        login(USER_ADMIN);

        assertHasWorkToDo(getPortfolio(PORTFOLIO_15));
        doPost(PORTFOLIO_DELETE, portfolioWithId(PORTFOLIO_15));
        doPost(PORTFOLIO_RESTORE, getPortfolio(PORTFOLIO_15));
        assertWorkIsDone(getPortfolio(PORTFOLIO_15));

        restoreLearningObjectChanges(PORTFOLIO_15);
    }

    private void assertHasWorkToDo(LearningObject learningObject) {
        if (learningObject instanceof IMaterial) {
            assertTrue(learningObject.getBroken() > 0);
        }
        assertTrue(learningObject.getImproper() > 0);
        assertTrue(learningObject.getUnReviewed() > 0);
        assertFalse(learningObject.isDeleted());
    }

    private void assertWorkIsDone(LearningObject learningObject, boolean deleted) {
        if (learningObject instanceof IMaterial) {
            assertTrue(learningObject.getBroken() == 0);
        }
        assertTrue(learningObject.getImproper() == 0);
        assertTrue(learningObject.getUnReviewed() == 0);
        assertEquals(deleted, learningObject.isDeleted());
    }

    private void assertWorkIsDone(LearningObject learningObjectAfter) {
        assertWorkIsDone(learningObjectAfter, false);
    }

    private void restoreLearningObjectChanges(Long learningObjectId) {
        DbUtils.getTransaction().begin();
        testDao.restore(learningObjectId);
        DbUtils.closeTransaction();
    }
}
