package ee.hm.dop.rest.administration;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.dao.TestDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReportingReasonEnum;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.utils.DbUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.*;
import static java.lang.String.format;
import static org.junit.Assert.*;

public class ReviewableChangeAdminResourceTest extends ResourceIntegrationTestBase {

    public static final String GET_ALL_CHANGES = "admin/changed/";
    public static final String GET_CHANGES_BY_ID = "admin/changed/%s";
    public static final String GET_CHANGED_COUNT = "admin/changed/count";
    public static final String ACCEPT_ALL_CHANGES_URL = "admin/changed/%s/acceptAll";
    public static final String ACCEPT_ONE_CHANGES_URL = "admin/changed/%s/acceptOne/%s";
    public static final String REVERT_ALL_CHANGES_URL = "admin/changed/%s/revertAll";
    public static final String REVERT_ONE_CHANGES_URL = "admin/changed/%s/revertOne/%s";
    public static final String SET_BROKEN = "material/setBroken";
    public static final String ADD_SYSTEM_TAG_URL = "learningObject/%s/system_tags";
    public static final String UPDATE_MATERIAL_URL = "material";
    public static final String SET_IMPROPER = "impropers";

    public static final String BIEBER_M16_ORIGINAL = "http://www.bieber.com";
    public static final String BEYONCE = "http://www.beyonce.com";
    public static final String MADONNA = "http://www.madonna.com";

    @Inject
    private TestDao testDao;
    @Inject
    private ReviewableChangeDao reviewableChangeDao;

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
        assertNotChanged(material, BIEBER_M16_ORIGINAL);
        material.setSource(BEYONCE);
        Material updateMaterial = createOrUpdateMaterial(material);
        assertChanged(updateMaterial, BEYONCE);

        revertUrl(updateMaterial);
    }

    @Test
    public void changes_are_not_registered_on_adding_same_source() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertNotChanged(material, BIEBER_M16_ORIGINAL);
        material.setSource(BIEBER_M16_ORIGINAL);
        Material updateMaterial = createOrUpdateMaterial(material);
        assertNotChanged(updateMaterial, BIEBER_M16_ORIGINAL);
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
        Material material = getMaterial(MATERIAL_16);
        assertDoesntHave(material, TAXON_MATHEMATICS_DOMAIN);
        doPut(format(ADD_SYSTEM_TAG_URL, MATERIAL_16), tag(TAXON_MATHEMATICS_DOMAIN.name));
        Material updatedMaterial = getMaterial(MATERIAL_16);
        assertHas(updatedMaterial, TAXON_MATHEMATICS_DOMAIN);

        doPost(format(ACCEPT_ALL_CHANGES_URL, MATERIAL_16));
        Material updatedMaterial2 = getMaterial(MATERIAL_16);

        updatedMaterial2.setTaxons(new ArrayList<>());
        Material updatedMaterial3 = createOrUpdateMaterial(updatedMaterial2);
        assertHasTagNotTaxon(updatedMaterial3, TAXON_MATHEMATICS_DOMAIN);
        ReviewableChange review = reviewableChangeDao.findByComboField("learningObject.id", MATERIAL_16);
        assertIsReviewed(review, USER_ADMIN);
    }

    @Test
    public void I_change_bieber_url_to_beyonce___material_has_beyonce_url_change_has_bieber() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertNotChanged(material, BIEBER_M16_ORIGINAL);
        material.setSource(BEYONCE);
        Material updateMaterial = createOrUpdateMaterial(material);
        assertChanged(updateMaterial, BEYONCE);
        ReviewableChange review = reviewableChangeDao.findByComboField("learningObject.id", MATERIAL_16);
        assertEquals(BIEBER_M16_ORIGINAL, review.getMaterialSource());

        revertUrl(updateMaterial);
    }

    @Test
    public void moderator_sees_changes_made_in_their_taxon_tree_only() throws Exception {
        long changedLearnigObjectsCount = doGet(GET_CHANGED_COUNT, Long.class);
        List<AdminLearningObject> reviewableChanges = doGet(GET_ALL_CHANGES, listOfAdminLOs());
        logout();

        login(USER_MODERATOR);
        long changedLearnigObjectsCount2 = doGet(GET_CHANGED_COUNT, Long.class);
        List<AdminLearningObject> reviewableChanges2 = doGet(GET_ALL_CHANGES, listOfAdminLOs());

        assertNotEquals(changedLearnigObjectsCount, changedLearnigObjectsCount2);
        assertNotEquals(reviewableChanges, reviewableChanges2);
    }

    @Test
    public void admin_can_accept_all_changes() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertDoesntHave(material, TAXON_MATHEMATICS_DOMAIN, TAXON_FOREIGNLANGUAGE_DOMAIN);
        doPut(format(ADD_SYSTEM_TAG_URL, MATERIAL_16), tag(TAXON_MATHEMATICS_DOMAIN.name));
        doPut(format(ADD_SYSTEM_TAG_URL, MATERIAL_16), tag(TAXON_FOREIGNLANGUAGE_DOMAIN.name));
        Material updatedMaterial = getMaterial(MATERIAL_16);
        assertHas(updatedMaterial, TAXON_MATHEMATICS_DOMAIN, TAXON_FOREIGNLANGUAGE_DOMAIN);

        doPost(format(ACCEPT_ALL_CHANGES_URL, MATERIAL_16));
        Material updatedMaterial1 = getMaterial(MATERIAL_16);
        assertTrue(updatedMaterial1.getChanged() == 0);
        List<ReviewableChange> review = reviewableChangeDao.findByComboFieldList("learningObject.id", MATERIAL_16);
        assertEquals(2, review.size());
        for (ReviewableChange change : review) {
            assertEquals(ReviewStatus.ACCEPTED, change.getStatus());
        }
    }

    @Test
    public void admin_can_revert_all_changes() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertDoesntHave(material, TAXON_MATHEMATICS_DOMAIN, TAXON_FOREIGNLANGUAGE_DOMAIN);
        doPut(format(ADD_SYSTEM_TAG_URL, MATERIAL_16), tag(TAXON_MATHEMATICS_DOMAIN.name));
        doPut(format(ADD_SYSTEM_TAG_URL, MATERIAL_16), tag(TAXON_FOREIGNLANGUAGE_DOMAIN.name));
        Material updatedMaterial = getMaterial(MATERIAL_16);
        assertHas(updatedMaterial, TAXON_MATHEMATICS_DOMAIN, TAXON_FOREIGNLANGUAGE_DOMAIN);

        doPost(format(REVERT_ALL_CHANGES_URL, MATERIAL_16));
        Material updatedMaterial1 = getMaterial(MATERIAL_16);
        assertTrue(updatedMaterial1.getChanged() == 0);
        List<ReviewableChange> review = reviewableChangeDao.findByComboFieldList("learningObject.id", MATERIAL_16);
        assertEquals(2, review.size());
        for (ReviewableChange change : review) {
            assertTrue(change.isReviewed());
            assertEquals(ReviewStatus.REJECTED, change.getStatus());
        }
        Material updatedMaterial2 = getMaterial(MATERIAL_16);
        assertTrue(updatedMaterial2.getTaxons().isEmpty());
    }

    @Test
    public void admin_can_revert_all_changes_url_edition() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertNotChanged(material, BIEBER_M16_ORIGINAL);
        material.setSource(BEYONCE);
        Material updateMaterial = createOrUpdateMaterial(material);
        assertChanged(updateMaterial, BEYONCE);

        doPost(format(REVERT_ALL_CHANGES_URL, MATERIAL_16));
        Material updatedMaterial1 = getMaterial(MATERIAL_16);
        assertNotChanged(updatedMaterial1, BIEBER_M16_ORIGINAL);

        DbUtils.getTransaction().begin();
        reviewableChangeDao.flush();
        DbUtils.closeTransaction();

        List<ReviewableChange> review2 = reviewableChangeDao.findByComboFieldList("learningObject.id", MATERIAL_16);
        assertEquals(1, review2.size());
        for (ReviewableChange change : review2) {
            assertTrue(change.isReviewed());
            assertEquals(ReviewStatus.REJECTED, change.getStatus());
        }
        Material updatedMaterial2 = getMaterial(MATERIAL_16);
        assertTrue(updatedMaterial2.getTaxons().isEmpty());
    }

    @Test
    public void admin_can_accept_one_change() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertDoesntHave(material, TAXON_MATHEMATICS_DOMAIN, TAXON_FOREIGNLANGUAGE_DOMAIN);
        doPut(format(ADD_SYSTEM_TAG_URL, MATERIAL_16), tag(TAXON_MATHEMATICS_DOMAIN.name));
        doPut(format(ADD_SYSTEM_TAG_URL, MATERIAL_16), tag(TAXON_FOREIGNLANGUAGE_DOMAIN.name));
        Material updatedMaterial = getMaterial(MATERIAL_16);
        assertHas(updatedMaterial, TAXON_MATHEMATICS_DOMAIN, TAXON_FOREIGNLANGUAGE_DOMAIN);

        List<ReviewableChange> reviewableChanges = doGet(format(GET_CHANGES_BY_ID, MATERIAL_16), listOfChanges());
        ReviewableChange oneChange = reviewableChanges.get(0);

        doPost(format(ACCEPT_ONE_CHANGES_URL, MATERIAL_16, oneChange.getId()));
        Material updatedMaterial1 = getMaterial(MATERIAL_16);
        assertFalse(updatedMaterial1.getChanged() == 0);
        List<ReviewableChange> review = reviewableChangeDao.findByComboFieldList("learningObject.id", MATERIAL_16);
        assertEquals(2, review.size());
        for (ReviewableChange change : review) {
            if (change.getId().equals(oneChange.getId())) {
                assertTrue(change.isReviewed());
                assertEquals(ReviewStatus.ACCEPTED, change.getStatus());
            } else {
                assertFalse(change.isReviewed());
            }
        }
        Material updatedMaterial2 = getMaterial(MATERIAL_16);
        assertEquals(2, updatedMaterial2.getTaxons().size());
    }

    @Test
    public void admin_can_revert_one_change() throws Exception {
        Material material = getMaterial(MATERIAL_16);
        assertDoesntHave(material, TAXON_MATHEMATICS_DOMAIN, TAXON_FOREIGNLANGUAGE_DOMAIN);
        doPut(format(ADD_SYSTEM_TAG_URL, MATERIAL_16), tag(TAXON_MATHEMATICS_DOMAIN.name));
        doPut(format(ADD_SYSTEM_TAG_URL, MATERIAL_16), tag(TAXON_FOREIGNLANGUAGE_DOMAIN.name));
        Material updatedMaterial = getMaterial(MATERIAL_16);
        assertHas(updatedMaterial, TAXON_MATHEMATICS_DOMAIN, TAXON_FOREIGNLANGUAGE_DOMAIN);

        List<ReviewableChange> reviewableChanges = doGet(format(GET_CHANGES_BY_ID, MATERIAL_16), listOfChanges());
        ReviewableChange oneChange = reviewableChanges.get(0);

        doPost(format(REVERT_ONE_CHANGES_URL, MATERIAL_16, oneChange.getId()));

        Material updatedMaterial1 = getMaterial(MATERIAL_16);
        assertFalse(updatedMaterial1.getChanged() == 0);
        List<ReviewableChange> review = reviewableChangeDao.findByComboFieldList("learningObject.id", MATERIAL_16);
        assertEquals(2, review.size());
        for (ReviewableChange change : review) {
            if (change.getId().equals(oneChange.getId())) {
                assertTrue(change.isReviewed());
                assertEquals(ReviewStatus.REJECTED, change.getStatus());
            } else {
                assertFalse(change.isReviewed());
            }
        }
        Material updatedMaterial2 = getMaterial(MATERIAL_16);
        if (oneChange.getTaxon().getId().equals(TAXON_FOREIGNLANGUAGE_DOMAIN.id)) {
            assertHasChangesDontMatter(updatedMaterial2, TAXON_MATHEMATICS_DOMAIN);
            assertHasTagNotTaxonChangesDontMatter(updatedMaterial2, TAXON_FOREIGNLANGUAGE_DOMAIN);
        } else {
            assertHasTagNotTaxonChangesDontMatter(updatedMaterial2, TAXON_MATHEMATICS_DOMAIN);
            assertHasChangesDontMatter(updatedMaterial2, TAXON_FOREIGNLANGUAGE_DOMAIN);
        }
    }

    private GenericType<List<ReviewableChange>> listOfChanges() {
        return new GenericType<List<ReviewableChange>>() {
        };
    }

    private GenericType<List<AdminLearningObject>> listOfAdminLOs() {
        return new GenericType<List<AdminLearningObject>>() {
        };
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

    private void revertUrl(Material material) {
        material.setSource(BIEBER_M16_ORIGINAL);
        createOrUpdateMaterial(material);
    }
}
