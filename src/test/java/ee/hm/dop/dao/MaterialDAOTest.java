package ee.hm.dop.dao;

import ee.hm.dop.common.test.GuiceTestRunner;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.utils.DbUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import javax.inject.Inject;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.*;

@RunWith(GuiceTestRunner.class)
public class MaterialDAOTest {

    @Inject
    private MaterialDAO materialDAO;

    @After
    public void closeEntityManager() {
        DbUtils.closeEntityManager();
    }

    @Test
    public void findAll() {
        List<Material> materials = materialDAO.findAll();
        assertEquals(8, materials.size());

        // Verify if all required fields are loaded
        for (int i = 0; i < materials.size(); i++) {
            Material material = materials.get(i);
            assertEquals(Long.valueOf(i + 1), material.getId());
            assertFalse(isBlank(material.getTitle()));
            List<Author> authors = material.getAuthors();
            assertNotNull(authors);

            for (Author author : authors) {
                assertNotNull(author.getId());
                assertFalse(isBlank(author.getName()));
                assertFalse(isBlank(author.getSurname()));
            }
            List<LanguageString> descriptions = material.getDescriptions();
            assertNotNull(descriptions);

            for (LanguageString languageDescription : descriptions) {
                assertNotNull(languageDescription.getId());
                assertFalse(isBlank(languageDescription.getlanguage()));
                assertFalse(isBlank(languageDescription.getDescription()));
            }
        }
    }

    @Test public void find() {
        long a = 1;
        Material material = materialDAO.find(a);

        assertEquals("Mathematics textbook for 9th grade", material.getTitle());
        assertEquals("EST", material.getDescriptions().get(0).getlanguage());
    }
}
