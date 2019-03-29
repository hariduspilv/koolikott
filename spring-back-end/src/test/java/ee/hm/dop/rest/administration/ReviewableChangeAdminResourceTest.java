package ee.hm.dop.rest.administration;

import com.google.common.collect.Lists;
import javax.inject.Inject;
import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.dao.TestDao;
import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.service.content.dto.TagDTO;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.assertChanged;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.assertDoesntHave;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.assertHas;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.assertHasChangesDontMatter;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.assertHasNoTagsNoTaxonsChangesAre1;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.assertHasNoTaxonNoTag;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.assertHasTagNotTaxon;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.assertHasTaxonNotTag;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.assertIsReviewed;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.assertNotChanged;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.improper;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@Ignore
@Transactional
public class ReviewableChangeAdminResourceTest extends ResourceIntegrationTestBase {

    public static final String GET_ALL_CHANGES = "admin/changed/";
    public static final String GET_CHANGES_BY_ID = "admin/changed/%s";
    public static final String GET_CHANGED_COUNT = "admin/changed/count";
    public static final String ACCEPT_ALL_CHANGES_URL = "admin/changed/%s/acceptAll";
    public static final String ACCEPT_ONE_CHANGES_URL = "admin/changed/%s/acceptOne/%s";
    public static final String REVERT_ALL_CHANGES_URL = "admin/changed/%s/revertAll";
    public static final String REVERT_ONE_CHANGES_URL = "admin/changed/%s/revertOne/%s";
    public static final String SET_IMPROPER = "impropers";

    public static final String BIEBER_ORIGINAL = "http://www.bieber%s.com";
    public static final String BEYONCE_ORIGINAL = "http://www.beyonce%s.com";
    public static final String MADONNA_ORIGINAL = "http://www.madonna%s.com";
    private static final boolean doWierdFlakyStuff = false;

    @Inject
    private TestDao testDao;
    @Inject
    private ReviewableChangeDao reviewableChangeDao;

    @Before
    public void setUp() {
        login(USER_ADMIN);
    }

    @Test
    public void changes_are_registered_on_new_source() {
        Material material = getMaterial(MATERIAL_18);
        assertNotChanged(material, format(BIEBER_ORIGINAL, material.getId()));
        material.setSource(BEYONCE_ORIGINAL);
        Material updateMaterial = createOrUpdateMaterial(material);
        assertChanged(updateMaterial, BEYONCE_ORIGINAL);
    }

    @Test
    public void changes_are_not_registered_on_adding_same_source() {
        Material material = getMaterial(MATERIAL_19);
        assertNotChanged(material, format(BIEBER_ORIGINAL, material.getId()));
        material.setSource(format(BIEBER_ORIGINAL, material.getId()));
        Material updateMaterial = createOrUpdateMaterial(material);
        assertNotChanged(updateMaterial, format(BIEBER_ORIGINAL, material.getId()));
    }

    @Test
    public void changes_are_not_registered_when_LO_is_improper() {
        Material material = getMaterial(MATERIAL_20);
        assertDoesntHave(material);
        doPut(SET_IMPROPER, improper(material));
        Material updatedMaterial = getMaterial(MATERIAL_20);
        assertHas(updatedMaterial, ReviewType.IMPROPER);
    }

    @Test
    public void changes_are_not_registered_when_LO_is_unreviewed() {
        Material material = getMaterial(MATERIAL_22);
        assertDoesntHave(material);
        setUnreviewed(Lists.newArrayList(MATERIAL_22));
        Material updatedMaterial = getMaterial(MATERIAL_22);
        assertHas(updatedMaterial, ReviewType.FIRST);
    }


    @Test
    public void moderator_sees_changes_made_in_their_taxon_tree_only() {
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
    public void admin_can_revert_all_changes() {
        Material material = getMaterial(MATERIAL_26);
        assertDoesntHave(material, TAXON_MATHEMATICS_DOMAIN, TAXON_FOREIGNLANGUAGE_DOMAIN);

        Material updatedMaterial1 = doPost(format(REVERT_ALL_CHANGES_URL, MATERIAL_26), null, Material.class);
        assertTrue(updatedMaterial1.getChanged() == 0);
        if (doWierdFlakyStuff) {
            List<ReviewableChange> review = reviewableChangeDao.findByComboFieldList("learningObject.id", MATERIAL_26);
            assertEquals(2, review.size());
            for (ReviewableChange change : review) {
                assertTrue(change.isReviewed());
                assertEquals(ReviewStatus.REJECTED, change.getStatus());
            }
        }
        Material updatedMaterial2 = getMaterial(MATERIAL_26);
        assertTrue(updatedMaterial2.getTaxons().isEmpty());
    }

    @Test
    public void I_change_bieber_url_to_beyonce_then_to_madonna___material_has_madonna_url_change_has_bieber() {
        Material material1 = getMaterial(MATERIAL_29);
        assertNotChanged(material1, format(BIEBER_ORIGINAL, MATERIAL_29));

        material1.setSource(format(BEYONCE_ORIGINAL, MATERIAL_29));
        Material material2 = createOrUpdateMaterial(material1);
        assertChanged(material2, format(BEYONCE_ORIGINAL, MATERIAL_29));

        material2.setSource(format(MADONNA_ORIGINAL, MATERIAL_29));
        Material material3 = createOrUpdateMaterial(material2);
        assertChanged(material3, format(MADONNA_ORIGINAL, MATERIAL_29));

        if (doWierdFlakyStuff) {
            ReviewableChange review = reviewableChangeDao.findByComboField("learningObject.id", MATERIAL_29);
            assertEquals(format(BIEBER_ORIGINAL, MATERIAL_29), review.getMaterialSource());
        }
    }

    @Test
    public void I_add_new_source_then_remove_it___change_is_removed() {
        Material material1 = getMaterial(MATERIAL_30);
        assertNotChanged(material1, format(BIEBER_ORIGINAL, MATERIAL_30));
        material1.setSource(format(BEYONCE_ORIGINAL, MATERIAL_30));
        Material material2 = createOrUpdateMaterial(material1);
        material2.setSource(format(BIEBER_ORIGINAL, MATERIAL_30));
        assertChanged(material2, format(BIEBER_ORIGINAL, MATERIAL_30));
        Material material3 = createOrUpdateMaterial(material2);
        assertNotChanged(material3, format(BIEBER_ORIGINAL, MATERIAL_30));

        if (doWierdFlakyStuff) {
            List<ReviewableChange> review2 = reviewableChangeDao.findByComboFieldList("learningObject.id", MATERIAL_30);
            assertEquals(1, review2.size());
            for (ReviewableChange change : review2) {
                assertTrue(change.isReviewed());
                assertEquals(ReviewStatus.OBSOLETE, change.getStatus());
            }
        }
    }

    @Test
    public void admin_can_revert_all_changes_url_edition() {
        Material material = getMaterial(MATERIAL_31);
        assertNotChanged(material, format(BIEBER_ORIGINAL, MATERIAL_31));
        material.setSource(format(BEYONCE_ORIGINAL, MATERIAL_31));
        Material updateMaterial = createOrUpdateMaterial(material);
        assertChanged(updateMaterial, format(BEYONCE_ORIGINAL, MATERIAL_31));

        Material updatedMaterial1 = doPost(format(REVERT_ALL_CHANGES_URL, MATERIAL_31), null, Material.class);
        assertNotChanged(updatedMaterial1, format(BIEBER_ORIGINAL, MATERIAL_31));

        if (doWierdFlakyStuff) {
            List<ReviewableChange> review2 = reviewableChangeDao.findByComboFieldList("learningObject.id", MATERIAL_31);
            assertEquals(1, review2.size());
            for (ReviewableChange change : review2) {
                assertTrue(change.isReviewed());
                assertEquals(ReviewStatus.REJECTED, change.getStatus());
            }
        }
        Material updatedMaterial2 = getMaterial(MATERIAL_31);
        assertTrue(updatedMaterial2.getTaxons().isEmpty());
    }

    @Test
    public void I_change_bieber_url_to_beyonce___material_has_beyonce_url_change_has_bieber() {
        Material material = getMaterial(MATERIAL_32);
        assertNotChanged(material, format(BIEBER_ORIGINAL, material.getId()));
        material.setSource(format(BEYONCE_ORIGINAL, material.getId()));
        Material updateMaterial = createOrUpdateMaterial(material);
        assertChanged(updateMaterial, format(BEYONCE_ORIGINAL, material.getId()));
        if (doWierdFlakyStuff) {
            ReviewableChange review = reviewableChangeDao.findByComboField("learningObject.id", MATERIAL_32);
            assertEquals(format(BIEBER_ORIGINAL, material.getId()), review.getMaterialSource());
        }
    }

    @Test
    public void I_change_bieber_url_to_beyonce_it_is_reviewed_then_I_change_it_to_madonna___material_has_madonna_url_1change_is_reviewed_with_beyonce_1change_unreviewed_with_madonna() {
        Material material1 = getMaterial(MATERIAL_33);
        assertNotChanged(material1, format(BIEBER_ORIGINAL, MATERIAL_33));

        material1.setSource(format(BEYONCE_ORIGINAL, MATERIAL_33));
        Material material2 = createOrUpdateMaterial(material1);
        assertChanged(material2, format(BEYONCE_ORIGINAL, MATERIAL_33));

        Material material3 = doPost(format(ACCEPT_ALL_CHANGES_URL, MATERIAL_33), null, Material.class);
        assertTrue(material3.getChanged() == 0);

        material3.setSource(format(MADONNA_ORIGINAL, MATERIAL_33));
        Material material4 = createOrUpdateMaterial(material3);
        assertChanged(material4, format(MADONNA_ORIGINAL, MATERIAL_33));

        if (doWierdFlakyStuff) {
            List<ReviewableChange> review = reviewableChangeDao.findByComboFieldList("learningObject.id", MATERIAL_33);
            Map<Boolean, List<ReviewableChange>> collect = review.stream().collect(Collectors.partitioningBy(ReviewableChange::isReviewed));

            List<ReviewableChange> reviewedChanges = collect.get(true);
            assertTrue("reviewed changes are not empty", isNotEmpty(reviewedChanges));
            if (isNotEmpty(reviewedChanges)) {
                ReviewableChange reviewed = reviewedChanges.get(0);
                assertIsReviewed(reviewed, USER_ADMIN);
            }
            List<ReviewableChange> unReviewedChanges = collect.get(false);
            assertTrue("UNreviewed changes are not empty", isNotEmpty(unReviewedChanges));
            if (isNotEmpty(unReviewedChanges)) {
                ReviewableChange unReviewed = unReviewedChanges.get(0);
                assertEquals(format(BEYONCE_ORIGINAL, MATERIAL_33), unReviewed.getMaterialSource());
            }
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

    private void setUnreviewed(List<Long> learningObjectId) {
        testDao.setUnReviewed(learningObjectId);
    }
}
