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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTest.*;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.assertChanged;
import static ee.hm.dop.rest.administration.ReviewableChangeAdminResourceTestUtil.assertIsReviewed;
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

        revertUrl(material3);
    }

    @Test
    public void I_change_bieber_url_to_beyonce_it_is_reviewed_then_I_change_it_to_madonna___material_has_madonna_url_1change_is_reviewed_with_beyonce_1change_unreviewed_with_madonna
            () throws Exception {
        Material material1 = getMaterial(MATERIAL_17);
        assertNotChanged(material1, BIEBER_M17_ORIGINAL);

        material1.setSource(BEYONCE17);
        Material material2 = createOrUpdateMaterial(material1);
        assertChanged(material2, BEYONCE17);

        doPost(format(ACCEPT_ALL_CHANGES_URL, MATERIAL_17));
        Material material3 = getMaterial(MATERIAL_17);
        assertTrue(material3.getChanged() == 0);

        material3.setSource(MADONNA17);
        Material material4 = createOrUpdateMaterial(material3);
        assertChanged(material4, MADONNA17);

        List<ReviewableChange> review = reviewableChangeDao.findByComboFieldList("learningObject.id", MATERIAL_17);
        Map<Boolean, List<ReviewableChange>> collect = review.stream().collect(Collectors.partitioningBy(ReviewableChange::isReviewed));
        ReviewableChange reviewed = collect.get(true).get(0);
        assertIsReviewed(reviewed, USER_ADMIN);
        ReviewableChange unReviewed = collect.get(false).get(0);
        assertEquals(BEYONCE17, unReviewed.getMaterialSource());

        revertUrl(material4);
    }


    private void restoreLearningObjectChanges(List<Long> learningObjectId) {
        DbUtils.getTransaction().begin();
        testDao.removeChanges(learningObjectId);
        DbUtils.closeTransaction();
    }


    private void revertUrl(Material material) {
        material.setSource(BIEBER_M17_ORIGINAL);
        createOrUpdateMaterial(material);
    }
}
