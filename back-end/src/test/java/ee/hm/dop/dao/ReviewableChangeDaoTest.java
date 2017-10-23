package ee.hm.dop.dao;

import com.google.inject.Inject;
import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.model.ReviewableChange;
import ee.hm.dop.model.Material;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class ReviewableChangeDaoTest extends DatabaseTestBase {

    public static final Material MATERIAL1 = TestConstants.materialWithId(MATERIAL_1);
    public static final Material MATERIAL2 = TestConstants.materialWithId(MATERIAL_2);
    @Inject
    private ReviewableChangeDao reviewableChangeDao;

    @Test
    public void getAllByLearningObject() {
        ReviewableChange change1 = change(MATERIAL1, 1L);
        ReviewableChange change2 = change(MATERIAL1, 2L);

        reviewableChangeDao.createOrUpdate(change1);
        reviewableChangeDao.createOrUpdate(change2);

        List<ReviewableChange> changes = reviewableChangeDao.getAllByLearningObject(MATERIAL_1);
        assertTrue(changes.size() == 2);

        reviewableChangeDao.removeAllByLearningObject(MATERIAL_1);

        List<ReviewableChange> changesAfter = reviewableChangeDao.getAllByLearningObject(MATERIAL_1);
        assertTrue(changesAfter.size() == 0);
    }

    @Test
    public void findAll() {
        ReviewableChange change1 = change(MATERIAL1, 1L);
        ReviewableChange change2 = change(MATERIAL1, 2L);
        ReviewableChange change3 = change(MATERIAL2, 3L);

        reviewableChangeDao.createOrUpdate(change1);
        reviewableChangeDao.createOrUpdate(change2);
        reviewableChangeDao.createOrUpdate(change3);

        List<ReviewableChange> changes = reviewableChangeDao.findAll();
        assertTrue(changes.size() == 3);

        reviewableChangeDao.removeAllByLearningObject(MATERIAL_1);
        reviewableChangeDao.removeAllByLearningObject(MATERIAL_2);

        List<ReviewableChange> changesAfter = reviewableChangeDao.findAll();
        assertTrue(changesAfter.size() == 0);
    }

    @Test
    public void removeById() {
        ReviewableChange change1 = new ReviewableChange();
        ReviewableChange change2 = new ReviewableChange();
        ReviewableChange change3 = new ReviewableChange();

        ReviewableChange saved1 = reviewableChangeDao.createOrUpdate(change1);
        ReviewableChange saved2 = reviewableChangeDao.createOrUpdate(change2);
        ReviewableChange saved3 = reviewableChangeDao.createOrUpdate(change3);

        List<ReviewableChange> changes = reviewableChangeDao.findAll();
        assertTrue(changes.size() == 3);

        assertTrue(reviewableChangeDao.removeById(saved1.getId()));
        assertTrue(reviewableChangeDao.removeById(saved2.getId()));
        assertTrue(reviewableChangeDao.removeById(saved3.getId()));

        List<ReviewableChange> changesAfter = reviewableChangeDao.findAll();
        assertTrue(changesAfter.size() == 0);
    }


    public ReviewableChange change(Material material, long id) {
        ReviewableChange change1 = new ReviewableChange();
        change1.setId(id);
        change1.setLearningObject(material);
        return change1;
    }
}
