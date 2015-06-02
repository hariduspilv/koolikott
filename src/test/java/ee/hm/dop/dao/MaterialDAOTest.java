package ee.hm.dop.dao;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.common.test.GuiceTestRunner;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.Material;

@RunWith(GuiceTestRunner.class)
public class MaterialDAOTest {

    @Inject
    private MaterialDAO materialDAO;

    @Test
    public void findAll() {
        List<Material> materials = materialDAO.findAll();
        assertEquals(8, materials.size());

        // Verify if all required fields are loaded
        for (int i = 0; i < materials.size(); i++) {
            Material material = materials.get(i);
            assertEquals(i + 1, material.getId());
            assertFalse(isBlank(material.getTitle()));
            List<Author> authors = material.getAuthors();
            assertNotNull(authors);

            for (Author author : authors) {
                assertNotNull(author.getId());
                assertFalse(isBlank(author.getName()));
                assertFalse(isBlank(author.getSurname()));
            }
        }
    }
}
