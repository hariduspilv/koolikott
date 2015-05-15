package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.common.test.GuiceTestRunner;
import ee.hm.dop.model.Material;

@RunWith(GuiceTestRunner.class)
public class MaterialDAOTest {

    @Inject
    private MaterialDAO materialDAO;

    @Test
    public void findAll() {
        List<Material> materials = materialDAO.findAll();
        assertEquals(3, materials.size());
    }
}
