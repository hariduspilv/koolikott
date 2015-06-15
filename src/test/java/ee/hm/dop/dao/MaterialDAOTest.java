package ee.hm.dop.dao;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.Material;
import ee.hm.dop.utils.DbUtils;

public class MaterialDAOTest extends DatabaseTestBase {

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
            assertFalse(isBlank(material.getSource()));

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
                assertNotNull(languageDescription.getLanguage());
                assertFalse(isBlank(languageDescription.getText()));
            }
        }
    }

    @Test
    public void find() {
        long a = 1;
        Material material = materialDAO.find(a);

        assertEquals("Matemaatika õpik üheksandale klassile", material.getTitle());
        Language language = material.getDescriptions().get(0).getLanguage();
        assertNotNull(language);
        assertEquals("est", language.getCode());
        assertEquals("Estonian", language.getName());
        assertEquals("et", language.getCodes().get(0));
    }

    @Test
    public void testAuthorAndDesc() {
        Material material = materialDAO.find(1);
        assertEquals(1, material.getAuthors().size());
        assertEquals("Isaac", material.getAuthors().get(0).getName());
        assertEquals("John Newton", material.getAuthors().get(0).getSurname());
        assertEquals("Test description in estonian. (Russian available)", material.getDescriptions().get(0).getText());
    }

    @Test
    public void testAuthors() {
        Material material = materialDAO.find(2);
        assertEquals(2, material.getAuthors().size());
        assertEquals("Isaac", material.getAuthors().get(0).getName());
        assertEquals("John Newton", material.getAuthors().get(0).getSurname());
        assertEquals("Leonardo", material.getAuthors().get(1).getName());
        assertEquals("Fibonacci", material.getAuthors().get(1).getSurname());
    }

    @Test
    public void testMaterialLanguage() {
        Material material1 = materialDAO.find(2);
        assertEquals("rus", material1.getLanguage().getCode());

        Material material2 = materialDAO.find(1);
        assertEquals("est", material2.getLanguage().getCode());
    }
}
