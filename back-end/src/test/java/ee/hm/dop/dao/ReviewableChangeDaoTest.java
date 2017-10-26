package ee.hm.dop.dao;

import com.google.inject.Inject;
import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.User;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;

public class ReviewableChangeDaoTest extends DatabaseTestBase {

    public static final Material MATERIAL1 = TestConstants.materialWithId(MATERIAL_1);
    public static final Material MATERIAL2 = TestConstants.materialWithId(MATERIAL_2);
    public static final Material MATERIAL3 = TestConstants.materialWithId(MATERIAL_3);
    @Inject
    private ReviewableChangeDao reviewableChangeDao;
    @Inject
    private UserDao userDao;

    @Test
    public void getAllByLearningObject() {
        ReviewableChange change1 = change(MATERIAL3);
        ReviewableChange change2 = change(MATERIAL3);

        reviewableChangeDao.createOrUpdate(change1);
        reviewableChangeDao.createOrUpdate(change2);

        List<ReviewableChange> changes = reviewableChangeDao.getAllByLearningObject(MATERIAL_3);
        assertTrue(changes.size() == 2);
    }

    @Test
    public void findAll() {
        ReviewableChange change1 = change(MATERIAL1);
        ReviewableChange change2 = change(MATERIAL1);
        ReviewableChange change3 = change(MATERIAL2);

        ReviewableChange change1Db = reviewableChangeDao.createOrUpdate(change1);
        ReviewableChange change2Db = reviewableChangeDao.createOrUpdate(change2);
        ReviewableChange change3Db = reviewableChangeDao.createOrUpdate(change3);

        List<ReviewableChange> changes = reviewableChangeDao.findAll();
        assertTrue(changes.containsAll(Arrays.asList(change1Db, change2Db, change3Db)));
        List<Long> ids = changes.stream().map(ReviewableChange::getId).collect(Collectors.toList());
        assertTrue(ids.containsAll(Arrays.asList(change1Db.getId(), change2Db.getId(), change3Db.getId())));
    }

    @Test
    public void query_doesnt_fail() throws Exception {
        reviewableChangeDao.findAllUnreviewed2();
        User moderator = userDao.findById(USER_MODERATOR.id);
        reviewableChangeDao.findAllUnreviewed2(moderator);
    }

    public ReviewableChange change(Material material) {
        ReviewableChange change1 = new ReviewableChange();
        change1.setLearningObject(material);
        return change1;
    }
}
