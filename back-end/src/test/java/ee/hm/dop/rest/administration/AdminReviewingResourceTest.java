package ee.hm.dop.rest.administration;

import com.google.inject.Inject;
import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.dao.TestDao;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.model.interfaces.IMaterial;
import ee.hm.dop.utils.DbUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class AdminReviewingResourceTest extends ResourceIntegrationTestBase {

    private static final String MATERIAL_SET_NOT_BROKEN = "admin/brokenContent/setNotBroken";
    private static final String LO_SET_NOT_IMPROPER = "admin/improper/setProper?learningObject=%s";
    private static final String LO_SET_FIRST_REVIEWED = "admin/firstReview/setReviewed";
    private static final String LO_ACCEPT_CHANGES = "admin/changed/%s/acceptAll";
    private static final String LO_REJECT_CHANGES = "admin/changed/%s/revertAll";
    private static final String MATERIAL_DELETE = "material/";
    private static final String MATERIAL_RESTORE = "admin/deleted/material/restore";
    private static final String PORTFOLIO_DELETE = "portfolio/delete";
    private static final String PORTFOLIO_RESTORE = "admin/deleted/portfolio/restore";

    @Inject
    private TestDao testDao;

    @Before
    public void setUp() throws Exception {
        login(USER_ADMIN);
    }

    @After
    public void tearDown() throws Exception {
        restoreLearningObjectChanges(Arrays.asList(MATERIAL_15, PORTFOLIO_15));
    }

    @Test
    public void restoring_material_approves_everything() throws Exception {
        assertHasWorkToDo(getMaterial(MATERIAL_15));
        doDelete(MATERIAL_DELETE + MATERIAL_15);
        doPost(MATERIAL_RESTORE, getMaterial(MATERIAL_15));
        assertWorkIsDone(getMaterial(MATERIAL_15), ReviewType.SYSTEM_RESTORE);
    }

    @Test
    public void restoring_portfolio_approves_everything() throws Exception {
        assertHasWorkToDo(getPortfolio(PORTFOLIO_15));
        doPost(PORTFOLIO_DELETE, portfolioWithId(PORTFOLIO_15));
        doPost(PORTFOLIO_RESTORE, getPortfolio(PORTFOLIO_15));
        assertWorkIsDone(getPortfolio(PORTFOLIO_15), ReviewType.SYSTEM_RESTORE);
    }

    @Test
    public void deleting_material_approves_everything() throws Exception {
        assertHasWorkToDo(getMaterial(MATERIAL_15));
        doDelete(MATERIAL_DELETE + MATERIAL_15);
        assertWorkIsDone(getMaterial(MATERIAL_15), ReviewType.SYSTEM_DELETE);
    }

    @Test
    public void deleting_portfolio_approves_everything() throws Exception {
        assertHasWorkToDo(getPortfolio(PORTFOLIO_15));
        doPost(PORTFOLIO_DELETE, portfolioWithId(PORTFOLIO_15));
        assertWorkIsDone(getPortfolio(PORTFOLIO_15), ReviewType.SYSTEM_DELETE);
    }

    @Test
    public void approving_improper_content_approves_everything() throws Exception {
        assertHasWorkToDo(getMaterial(MATERIAL_15));
        doDelete(format(LO_SET_NOT_IMPROPER, MATERIAL_15));
        assertWorkIsDone(getMaterial(MATERIAL_15), ReviewType.IMPROPER);
    }

    @Test
    public void approving_broken_content_approves_everything() throws Exception {
        assertHasWorkToDo(getMaterial(MATERIAL_15));
        doPost(MATERIAL_SET_NOT_BROKEN, getMaterial(MATERIAL_15));
        assertWorkIsDone(getMaterial(MATERIAL_15), ReviewType.BROKEN);
    }

    @Test
    public void approving_first_review_approves_changes_too() throws Exception {
        assertHasWorkToDo(getMaterial(MATERIAL_15));
        doPost(LO_SET_FIRST_REVIEWED, getMaterial(MATERIAL_15));
        assertWorkIsDone(getMaterial(MATERIAL_15), ReviewType.FIRST);
    }

    @Test
    public void approving_changes_approves_only_changes() throws Exception {
        assertHasWorkToDo(getMaterial(MATERIAL_15));
        doPost(format(LO_ACCEPT_CHANGES, MATERIAL_15));
        assertWorkIsDone(getMaterial(MATERIAL_15), ReviewType.CHANGE);
    }

    @Test
    public void rejecting_changes_reviews_only_changes() throws Exception {
        assertHasWorkToDo(getMaterial(MATERIAL_15));
        doPost(format(LO_REJECT_CHANGES, MATERIAL_15));
        assertWorkIsDone(getMaterial(MATERIAL_15), ReviewType.CHANGE);
    }

    private void assertHasWorkToDo(LearningObject learningObject) {
        if (learningObject instanceof IMaterial) {
            assertTrue(learningObject.getBroken() > 0);
        }
        assertTrue(learningObject.getImproper() > 0);
        assertTrue(learningObject.getUnReviewed() > 0);
        assertTrue(learningObject.getChanged() > 0);
        assertFalse(learningObject.isDeleted());
    }

    private void assertWorkIsDone(LearningObject learningObject, ReviewType reviewType) {
        if (reviewType != ReviewType.FIRST && reviewType != ReviewType.CHANGE) {
            if (learningObject instanceof IMaterial) {
                assertTrue(learningObject.getBroken() == 0);
            }
            assertTrue(learningObject.getImproper() == 0);
        } else {
            assertTrue(learningObject.getImproper() > 0);
        }
        if (reviewType != ReviewType.CHANGE){
            assertTrue(learningObject.getUnReviewed() == 0);
        } else {
            assertTrue(learningObject.getUnReviewed() > 0);
        }
        assertTrue(learningObject.getChanged() == 0);
        if (reviewType == ReviewType.SYSTEM_DELETE) {
            assertTrue(learningObject.isDeleted());
        } else {
            assertFalse(learningObject.isDeleted());
        }
    }

    private void restoreLearningObjectChanges(List<Long> learningObjectId) {
        DbUtils.getTransaction().begin();
        testDao.restore(learningObjectId);
        DbUtils.closeTransaction();
    }
}
