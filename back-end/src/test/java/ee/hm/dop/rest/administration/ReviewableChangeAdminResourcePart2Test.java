package ee.hm.dop.rest.administration;

import com.google.inject.Inject;
import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.dao.TestDao;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.utils.DbUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityTransaction;
import java.util.Arrays;
import java.util.List;

import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTest.BEYONCE;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTest.REVERT_ALL_CHANGES_URL;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.assertChanged;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.assertNotChanged;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReviewableChangeAdminResourcePart2Test extends ResourceIntegrationTestBase {

    public static final String BIEBER_M17_ORIGINAL = "http://www.bieber2.com";
    public static final String BEYONCE17 = "http://www.beyonce2.com";
    public static final String MADONNA17 = "http://www.madonna2.com";

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
        restoreLearningObjectChanges(Arrays.asList(MATERIAL_17));
    }

    @Test
    public void I_change_bieber_url_to_beyonce_then_to_madonna___material_has_madonna_url_change_has_bieber() throws Exception {
        Material material1 = getMaterial(MATERIAL_17);
        assertNotChanged(material1, BIEBER_M17_ORIGINAL);

        material1.setSource(BEYONCE17);
        Material material2 = createOrUpdateMaterial(material1);
        assertChanged(material2, BEYONCE17);

        material2.setSource(MADONNA17);
        Material material3 = createOrUpdateMaterial(material2);
        assertChanged(material3, MADONNA17);

        ReviewableChange review = reviewableChangeDao.findByComboField("learningObject.id", MATERIAL_17);
        assertEquals(BIEBER_M17_ORIGINAL, review.getMaterialSource());

        revertUrl(material3, BIEBER_M17_ORIGINAL);
    }

    @Test
    public void admin_can_revert_all_changes_url_edition() throws Exception {
        Material material = getMaterial(MATERIAL_17);
        assertNotChanged(material, BIEBER_M17_ORIGINAL);
        material.setSource(BEYONCE);
        Material updateMaterial = createOrUpdateMaterial(material);
        assertChanged(updateMaterial, BEYONCE);

        Material updatedMaterial1 = doPost(format(REVERT_ALL_CHANGES_URL, MATERIAL_17), null, Material.class);
        assertNotChanged(updatedMaterial1, BIEBER_M17_ORIGINAL);

        DbUtils.getTransaction().begin();
        reviewableChangeDao.flush();
        DbUtils.closeTransaction();

        List<ReviewableChange> review2 = reviewableChangeDao.findByComboFieldList("learningObject.id", MATERIAL_17);
        assertEquals(1, review2.size());
        for (ReviewableChange change : review2) {
            assertTrue(change.isReviewed());
            assertEquals(ReviewStatus.REJECTED, change.getStatus());
        }
        Material updatedMaterial2 = getMaterial(MATERIAL_17);
        assertTrue(updatedMaterial2.getTaxons().isEmpty());
    }

    @Test
    public void I_change_bieber_url_to_beyonce___material_has_beyonce_url_change_has_bieber() throws Exception {
        Material material = getMaterial(MATERIAL_17);
        assertNotChanged(material, BIEBER_M17_ORIGINAL);
        material.setSource(BEYONCE);
        Material updateMaterial = createOrUpdateMaterial(material);
        assertChanged(updateMaterial, BEYONCE);
        ReviewableChange review = reviewableChangeDao.findByComboField("learningObject.id", MATERIAL_17);
        assertEquals(BIEBER_M17_ORIGINAL, review.getMaterialSource());

        revertUrl(updateMaterial, BIEBER_M17_ORIGINAL);
    }

    private void restoreLearningObjectChanges(List<Long> learningObjectId) {
        EntityTransaction transaction = DbUtils.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        testDao.removeChanges(learningObjectId);
        DbUtils.closeTransaction();
    }

    private void revertUrl(Material material, String source) {
        material.setSource(source);
        createOrUpdateMaterial(material);
    }
}
