package ee.hm.dop.dao;

import com.google.inject.Inject;
import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.model.ChangedLearningObject;
import ee.hm.dop.model.Material;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class ChangedLearningObjectDaoTest extends DatabaseTestBase {

    public static final Material MATERIAL1 = TestConstants.materialWithId(MATERIAL_1);
    public static final Material MATERIAL2 = TestConstants.materialWithId(MATERIAL_2);
    @Inject
    private ChangedLearningObjectDao changedLearningObjectDao;

    @Test
    public void getAllByLearningObject() {
        ChangedLearningObject change1 = change(MATERIAL1, 1L);
        ChangedLearningObject change2 = change(MATERIAL1, 2L);

        changedLearningObjectDao.createOrUpdate(change1);
        changedLearningObjectDao.createOrUpdate(change2);

        List<ChangedLearningObject> changes = changedLearningObjectDao.getAllByLearningObject(MATERIAL_1);
        assertTrue(changes.size() == 2);

        changedLearningObjectDao.removeAllByLearningObject(MATERIAL_1);

        List<ChangedLearningObject> changesAfter = changedLearningObjectDao.getAllByLearningObject(MATERIAL_1);
        assertTrue(changesAfter.size() == 0);
    }

    @Test
    public void findAll() {
        ChangedLearningObject change1 = change(MATERIAL1, 1L);
        ChangedLearningObject change2 = change(MATERIAL1, 2L);
        ChangedLearningObject change3 = change(MATERIAL2, 3L);

        changedLearningObjectDao.createOrUpdate(change1);
        changedLearningObjectDao.createOrUpdate(change2);
        changedLearningObjectDao.createOrUpdate(change3);

        List<ChangedLearningObject> changes = changedLearningObjectDao.findAll();
        assertTrue(changes.size() == 3);

        changedLearningObjectDao.removeAllByLearningObject(MATERIAL_1);
        changedLearningObjectDao.removeAllByLearningObject(MATERIAL_2);

        List<ChangedLearningObject> changesAfter = changedLearningObjectDao.findAll();
        assertTrue(changesAfter.size() == 0);
    }

    @Test
    public void removeById() {
        ChangedLearningObject change1 = new ChangedLearningObject();
        ChangedLearningObject change2 = new ChangedLearningObject();
        ChangedLearningObject change3 = new ChangedLearningObject();

        ChangedLearningObject saved1 = changedLearningObjectDao.createOrUpdate(change1);
        ChangedLearningObject saved2 = changedLearningObjectDao.createOrUpdate(change2);
        ChangedLearningObject saved3 = changedLearningObjectDao.createOrUpdate(change3);

        List<ChangedLearningObject> changes = changedLearningObjectDao.findAll();
        assertTrue(changes.size() == 3);

        assertTrue(changedLearningObjectDao.removeById(saved1.getId()));
        assertTrue(changedLearningObjectDao.removeById(saved2.getId()));
        assertTrue(changedLearningObjectDao.removeById(saved3.getId()));

        List<ChangedLearningObject> changesAfter = changedLearningObjectDao.findAll();
        assertTrue(changesAfter.size() == 0);
    }


    public ChangedLearningObject change(Material material, long id) {
        ChangedLearningObject change1 = new ChangedLearningObject();
        change1.setId(id);
        change1.setLearningObject(material);
        return change1;
    }
}
