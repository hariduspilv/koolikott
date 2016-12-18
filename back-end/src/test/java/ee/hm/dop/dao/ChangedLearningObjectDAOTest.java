package ee.hm.dop.dao;

import com.google.inject.Inject;
import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.ChangedLearningObject;
import ee.hm.dop.model.Material;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class ChangedLearningObjectDAOTest extends DatabaseTestBase {

    @Inject
    private ChangedLearningObjectDAO changedLearningObjectDAO;

    @Test
    public void getAllByLearningObject() {
        ChangedLearningObject change1 = new ChangedLearningObject();
        ChangedLearningObject change2 = new ChangedLearningObject();

        Material material = new Material();
        material.setId(1L);

        change1.setId(1L);
        change2.setId(2L);
        change1.setLearningObject(material);
        change2.setLearningObject(material);

        changedLearningObjectDAO.update(change1);
        changedLearningObjectDAO.update(change2);

        List<ChangedLearningObject> changes = changedLearningObjectDAO.getAllByLearningObject(1);
        assertTrue(changes.size() == 2);

        changedLearningObjectDAO.removeAllByLearningObject(1);

        List<ChangedLearningObject> changesAfter = changedLearningObjectDAO.getAllByLearningObject(1);
        assertTrue(changesAfter.size() == 0);
    }

    @Test
    public void findAll() {
        ChangedLearningObject change1 = new ChangedLearningObject();
        ChangedLearningObject change2 = new ChangedLearningObject();
        ChangedLearningObject change3 = new ChangedLearningObject();

        Material material1 = new Material();
        material1.setId(1L);
        Material material2 = new Material();
        material2.setId(2L);

        change1.setId(1L);
        change2.setId(2L);
        change3.setId(3L);
        change1.setLearningObject(material1);
        change2.setLearningObject(material1);
        change3.setLearningObject(material2);

        changedLearningObjectDAO.update(change1);
        changedLearningObjectDAO.update(change2);
        changedLearningObjectDAO.update(change3);

        List<ChangedLearningObject> changes = changedLearningObjectDAO.findAll();
        assertTrue(changes.size() == 3);

        changedLearningObjectDAO.removeAllByLearningObject(1);
        changedLearningObjectDAO.removeAllByLearningObject(2);

        List<ChangedLearningObject> changesAfter = changedLearningObjectDAO.findAll();
        assertTrue(changesAfter.size() == 0);
    }

    @Test
    public void removeById() {
        ChangedLearningObject change1 = new ChangedLearningObject();
        ChangedLearningObject change2 = new ChangedLearningObject();
        ChangedLearningObject change3 = new ChangedLearningObject();

        ChangedLearningObject saved1 = changedLearningObjectDAO.update(change1);
        ChangedLearningObject saved2 = changedLearningObjectDAO.update(change2);
        ChangedLearningObject saved3 = changedLearningObjectDAO.update(change3);

        List<ChangedLearningObject> changes = changedLearningObjectDAO.findAll();
        assertTrue(changes.size() == 3);

        assertTrue(changedLearningObjectDAO.removeById(saved1.getId()));
        assertTrue(changedLearningObjectDAO.removeById(saved2.getId()));
        assertTrue(changedLearningObjectDAO.removeById(saved3.getId()));

        List<ChangedLearningObject> changesAfter = changedLearningObjectDAO.findAll();
        assertTrue(changesAfter.size() == 0);
    }

}
