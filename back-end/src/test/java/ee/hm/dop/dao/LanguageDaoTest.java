package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.enums.LanguageC;
import org.junit.Test;

public class LanguageDaoTest extends DatabaseTestBase {

    @Inject
    private LanguageDao languageDao;

    @Test
    public void findByCode() {
        Language language1 = languageDao.findByCode(LanguageC.EST);

        assertNotNull(language1);
        assertEquals(new Long(1), language1.getId());
        assertEquals(LanguageC.EST, language1.getCode());
        assertEquals("Estonian", language1.getName());
        assertEquals(1, language1.getCodes().size());
        assertEquals("et", language1.getCodes().get(0));

        Language language2 = languageDao.findByCode("fr");
        assertNotNull(language2);
        assertEquals(new Long(6), language2.getId());
        assertEquals("fre", language2.getCode());
        assertEquals("French", language2.getName());
        assertEquals(1, language2.getCodes().size());
        assertEquals("fr", language2.getCodes().get(0));
    }

    @Test
    public void findByCodePassingNull() {
        assertNull(languageDao.findByCode(null));
    }

    @Test
    public void findByCodePassingNotExistingLanguage() {
        assertNull(languageDao.findByCode("doesntExist"));
    }

    @Test
    public void findAll() {
        List<Language> languages = languageDao.findAll();
        assertEquals(6, languages.size());

        List<String> expectedNames = Arrays.asList("Estonian", "Russian", "English", "Arabic", "Portuguese", "French");
        List<String> actualNames = languages.stream().map(l -> l.getName()).collect(Collectors.toList());
        assertTrue(actualNames.containsAll(expectedNames));
    }
}
