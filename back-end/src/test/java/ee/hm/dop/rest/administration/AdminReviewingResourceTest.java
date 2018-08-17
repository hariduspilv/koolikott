package ee.hm.dop.rest.administration;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReviewType;
import org.junit.Before;
import org.junit.Test;

import static java.lang.String.format;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AdminReviewingResourceTest extends ResourceIntegrationTestBase {

    private static final String LO_SET_NOT_IMPROPER = "admin/improper/setProper";
    private static final String LO_SET_FIRST_REVIEWED = "admin/firstReview/setReviewed";
    private static final String LO_ACCEPT_CHANGES = "admin/changed/%s/acceptAll";
    private static final String LO_REJECT_CHANGES = "admin/changed/%s/revertAll";
    private static final String MATERIAL_DELETE = "material/delete";
    private static final String MATERIAL_RESTORE = "admin/deleted/restore";
    private static final String PORTFOLIO_DELETE = "portfolio/delete";
    private static final String PORTFOLIO_RESTORE = "admin/deleted/restore";

    @Before
    public void setUp() {
        login(USER_ADMIN);
    }

    @Test
    public void restoring_material_approves_everything() throws Exception {
        assertHasWorkToDo(getMaterial(MATERIAL_34));
        doPost(MATERIAL_DELETE, materialWithId(MATERIAL_34));
        Material restored = doPost(MATERIAL_RESTORE, materialWithId(MATERIAL_34), Material.class);
        assertWorkIsDone(restored, ReviewType.SYSTEM_RESTORE);
    }

    @Test
    public void restoring_portfolio_approves_everything() throws Exception {
        Portfolio initialPortfolio = getPortfolio(PORTFOLIO_15);
        assertHasWorkToDo(initialPortfolio);
        Portfolio deletedPortfolio = doPost(PORTFOLIO_DELETE, portfolioWithId(initialPortfolio.getId()), Portfolio.class);
        Portfolio restoredPortfolio = doPost(PORTFOLIO_RESTORE, portfolioWithId(deletedPortfolio.getId()), Portfolio.class);
        assertWorkIsDone(restoredPortfolio, ReviewType.SYSTEM_RESTORE);
    }

    @Test
    public void deleting_material_approves_everything() throws Exception {
        assertHasWorkToDo(getMaterial(MATERIAL_35));
        Material material = doPost(MATERIAL_DELETE, materialWithId(MATERIAL_35), Material.class);
        assertWorkIsDone(material, ReviewType.SYSTEM_DELETE);
    }

    @Test
    public void deleting_portfolio_approves_everything() throws Exception {
        assertHasWorkToDo(getPortfolio(PORTFOLIO_17));
        Portfolio portfolio = doPost(PORTFOLIO_DELETE, portfolioWithId(PORTFOLIO_17), Portfolio.class);
        assertWorkIsDone(portfolio, ReviewType.SYSTEM_DELETE);
    }

    @Test
    public void approving_improper_content_approves_everything() throws Exception {
        assertHasWorkToDo(getMaterial(MATERIAL_36));
        Material proper = doPost(LO_SET_NOT_IMPROPER, materialWithId(MATERIAL_36), Material.class);
        assertWorkIsDone(proper, ReviewType.IMPROPER);
    }

    @Test
    public void approving_first_review_approves_changes_too() throws Exception {
        assertHasWorkToDo(getMaterial(MATERIAL_37));
        Material reviewed = doPost(LO_SET_FIRST_REVIEWED, materialWithId(MATERIAL_37), Material.class);
        assertWorkIsDone(reviewed, ReviewType.FIRST);
    }

    @Test
    public void approving_changes_approves_only_changes() throws Exception {
        assertHasWorkToDo(getMaterial(MATERIAL_38));
        Material approvedChanges = doPost(format(LO_ACCEPT_CHANGES, MATERIAL_38), null, Material.class);
        assertWorkIsDone(approvedChanges, ReviewType.CHANGE);
    }

    @Test
    public void rejecting_changes_reviews_only_changes() throws Exception {
        assertHasWorkToDo(getMaterial(MATERIAL_39));
        Material rejectedChanges = doPost(format(LO_REJECT_CHANGES, MATERIAL_39), null, Material.class);
        assertWorkIsDone(rejectedChanges, ReviewType.CHANGE);
    }

    private void assertHasWorkToDo(LearningObject learningObject) {
        assertTrue(learningObject.getImproper() > 0);
        assertTrue(learningObject.getUnReviewed() > 0);
        assertTrue(learningObject.getChanged() > 0);
        assertFalse(learningObject.isDeleted());
    }

    private void assertWorkIsDone(LearningObject learningObject, ReviewType reviewType) {
        if (reviewType != ReviewType.FIRST && reviewType != ReviewType.CHANGE) {
            assertTrue(learningObject.getImproper() == 0);
        } else {
            assertTrue(learningObject.getImproper() > 0);
        }
        if (reviewType != ReviewType.CHANGE) {
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
}
