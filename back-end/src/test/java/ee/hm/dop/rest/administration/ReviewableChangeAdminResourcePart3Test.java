package ee.hm.dop.rest.administration;

import com.google.inject.Inject;
import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.dao.ReviewableChangeDao;
import ee.hm.dop.dao.TestDao;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.utils.DbUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityTransaction;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTest.ACCEPT_ALL_CHANGES_URL;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.*;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReviewableChangeAdminResourcePart3Test extends ResourceIntegrationTestBase {

    public static final String BIEBER_M18_ORIGINAL = "http://www.bieber3.com";
    public static final String BEYONCE18 = "http://www.beyonce3.com";
    public static final String MADONNA18 = "http://www.madonna3.com";

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
        restoreLearningObjectChanges(Arrays.asList(MATERIAL_18));
    }

    @Test
    public void I_change_bieber_url_to_beyonce_it_is_reviewed_then_I_change_it_to_madonna___material_has_madonna_url_1change_is_reviewed_with_beyonce_1change_unreviewed_with_madonna
            () throws Exception {
        Material material1 = getMaterial(MATERIAL_18);
        assertNotChanged(material1, BIEBER_M18_ORIGINAL);

        material1.setSource(BEYONCE18);
        Material material2 = createOrUpdateMaterial(material1);
        assertChanged(material2, BEYONCE18);

        Material material3 = doPost(format(ACCEPT_ALL_CHANGES_URL, MATERIAL_18), null, Material.class);
        assertTrue(material3.getChanged() == 0);

        material3.setSource(MADONNA18);
        Material material4 = createOrUpdateMaterial(material3);
        assertChanged(material4, MADONNA18);

        List<ReviewableChange> review = reviewableChangeDao.findByComboFieldList("learningObject.id", MATERIAL_18);
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
            assertEquals(BEYONCE18, unReviewed.getMaterialSource());
        }

        revertUrl(material4, BIEBER_M18_ORIGINAL);
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
