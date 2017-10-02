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

    @Inject
    private ChangedLearningObjectDao changedLearningObjectDao;

    @Test
    public void getAllByLearningObject() {
        ChangedLearningObject change1 = new ChangedLearningObject();
        ChangedLearningObject change2 = new ChangedLearningObject();

        Material material = new Material();
        material.setId(TestConstants.MATERIAL_1);

        change1.setId(TestConstants.MATERIAL_1);
        change2.setId(2L);
        change1.setLearningObject(material);
        change2.setLearningObject(material);

        changedLearningObjectDao.createOrUpdate(change1);
        changedLearningObjectDao.createOrUpdate(change2);

        List<ChangedLearningObject> changes = changedLearningObjectDao.getAllByLearningObject(1);
        assertTrue(changes.size() == 2);

        changedLearningObjectDao.removeAllByLearningObject(1);

        List<ChangedLearningObject> changesAfter = changedLearningObjectDao.getAllByLearningObject(1);
        assertTrue(changesAfter.size() == 0);
    }

    @Test
    public void findAll() {
        ChangedLearningObject change1 = new ChangedLearningObject();
        ChangedLearningObject change2 = new ChangedLearningObject();
        ChangedLearningObject change3 = new ChangedLearningObject();

        Material material1 = new Material();
        material1.setId(TestConstants.MATERIAL_1);
        Material material2 = new Material();
        material2.setId(2L);

        change1.setId(TestConstants.MATERIAL_1);
        change2.setId(2L);
        change3.setId(3L);
        change1.setLearningObject(material1);
        change2.setLearningObject(material1);
        change3.setLearningObject(material2);

        changedLearningObjectDao.createOrUpdate(change1);
        changedLearningObjectDao.createOrUpdate(change2);
        changedLearningObjectDao.createOrUpdate(change3);

        List<ChangedLearningObject> changes = changedLearningObjectDao.findAll();
        assertTrue(changes.size() == 3);

        changedLearningObjectDao.removeAllByLearningObject(1);
        changedLearningObjectDao.removeAllByLearningObject(2);

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

}
