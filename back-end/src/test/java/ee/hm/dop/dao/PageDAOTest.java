package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.Page;
import org.junit.Test;

public class PageDAOTest extends DatabaseTestBase {

    @Inject
    private PageDAO pageDAO;

    @Inject
    private LanguageDAO languageDAO;

    @Test
    public void findByNameAndLang() {
        String pageName = "Help";
        String pageLanguageCode = "eng";
        Language pageLanguage = languageDAO.findByCode(pageLanguageCode);

        Page page = pageDAO.findByNameAndLanguage(pageName, pageLanguage);

        assertEquals(Long.valueOf(6), page.getId());
        assertEquals(Long.valueOf(3), page.getLanguage().getId());
        assertEquals("Help", page.getName());
        assertEquals("<h1>Help</h1><p>Text here</p>", page.getContent());

    }

    @Test
    public void findByNameAndLangPassingNull() {
        String pageName = null;
        String pageLanguageCode = "eng";
        Language pageLanguage = languageDAO.findByCode(pageLanguageCode);

        assertNull(pageDAO.findByNameAndLanguage(pageName, pageLanguage));
    }

    @Test
    public void findByNameAndLangPassingNotExistingPage() {
        String pageName = "doesntExist";
        String pageLanguageCode = "eng";
        Language pageLanguage = languageDAO.findByCode(pageLanguageCode);

        assertNull(pageDAO.findByNameAndLanguage(pageName, pageLanguage));
    }

}
