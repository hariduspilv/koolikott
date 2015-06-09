package ee.hm.dop.dao;

import ee.hm.dop.common.test.GuiceTestRunner;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.LanguageDescription;
import ee.hm.dop.model.Material;
import ee.hm.dop.utils.DbUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

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
            List<LanguageDescription> descriptions = material.getDescriptions();
            assertNotNull(descriptions);

            for(LanguageDescription languageDescription : descriptions){
                assertNotNull(languageDescription.getId());
                assertFalse(isBlank(languageDescription.getDescriptionLanguage()));
                assertFalse(isBlank(languageDescription.getDescription()));
            }
        }
    }
}
