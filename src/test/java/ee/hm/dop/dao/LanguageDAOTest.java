package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Language;

public class LanguageDAOTest extends DatabaseTestBase {

    @Inject
    private LanguageDAO languageDAO;

    @Test
    public void findByCode() {
        Language language = languageDAO.findByCode("est");

        assertNotNull(language);
        assertEquals(new Long(1), language.getId());
        assertEquals("est", language.getCode());
        assertEquals("Estonian", language.getName());
        assertEquals(1, language.getCodes().size());
        assertEquals("et", language.getCodes().get(0));
    }

    @Test
    public void findByCodePassingNull() {
        assertNull(languageDAO.findByCode(null));
    }

    @Test
    public void findByCodePassingNotExistingLanguage() {
        assertNull(languageDAO.findByCode("doesntExist"));
    }
}
