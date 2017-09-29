package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.Page;
import ee.hm.dop.model.enums.LanguageC;
import org.junit.Test;

public class PageDaoTest extends DatabaseTestBase {

    @Inject
    private PageDao pageDao;

    @Inject
    private LanguageDao languageDao;

    @Test
    public void findByNameAndLang() {
        String pageName = "Help";
        String pageLanguageCode = LanguageC.ENG;
        Language pageLanguage = languageDao.findByCode(pageLanguageCode);

        Page page = pageDao.findByNameAndLanguage(pageName, pageLanguage);

        assertEquals(Long.valueOf(6), page.getId());
        assertEquals(Long.valueOf(3), page.getLanguage().getId());
        assertEquals("Help", page.getName());
        assertEquals("<h1>Help</h1><p>Text here</p>", page.getContent());

    }

    @Test
    public void findByNameAndLangPassingNull() {
        String pageName = null;
        String pageLanguageCode = LanguageC.ENG;
        Language pageLanguage = languageDao.findByCode(pageLanguageCode);

        assertNull(pageDao.findByNameAndLanguage(pageName, pageLanguage));
    }

    @Test
    public void findByNameAndLangPassingNotExistingPage() {
        String pageName = "doesntExist";
        String pageLanguageCode = LanguageC.ENG;
        Language pageLanguage = languageDao.findByCode(pageLanguageCode);

        assertNull(pageDao.findByNameAndLanguage(pageName, pageLanguage));
    }

}
