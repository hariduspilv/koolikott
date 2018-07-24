package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.TranslationGroup;
import ee.hm.dop.model.enums.LanguageC;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Map;

import static org.junit.Assert.*;

public class TranslationGroupDaoTest extends DatabaseTestBase {

    @Inject
    private TranslationGroupDao translationGroupDao;

    @Test
    public void getTranslationGroupForEstonian() {
        Language language = new Language();
        language.setId((long) 1);
        language.setCode(LanguageC.EST);

        TranslationGroup translationGroup = translationGroupDao.findTranslationGroupFor(language);

        assertNotNull(translationGroup);
        assertEquals(language.getCode(), translationGroup.getLanguage().getCode());
        assertEquals(language.getId(), translationGroup.getLanguage().getId());
        Map<String, String> translations = translationGroup.getTranslations();
        assertTrue(translations.size() >= 4);
        assertEquals("FOO sõnum", translations.get("FOO"));
        assertEquals("Eesti keeles", translations.get("Estonian"));
        assertEquals("Vene keel", translations.get("Russian"));
        assertEquals("Matemaatika", translations.get("TOPIC_MATHEMATICS"));
    }

    @Test
    public void getTranslationKeyByTranslation() {
        String translationKey = translationGroupDao.getTranslationKeyByTranslation("FOO sõnum");
        assertNotNull(translationKey);
        assertEquals("FOO", translationKey);
        String translationKey2 = translationGroupDao.getTranslationKeyByTranslation("FOO");
        assertNull(translationKey2);
    }

    @Test
    public void getTranslationByKeyAndLangcode() {
        String translation = translationGroupDao.getTranslationByKeyAndLangcode("FOO", 1L);
        assertNotNull(translation);
        assertEquals("FOO sõnum", translation);
        String translation2 = translationGroupDao.getTranslationByKeyAndLangcode("P", 1L);
        assertNull(translation2);
    }


    @Test
    public void getTranslationGroupForRussian() {
        Language language = new Language();
        language.setId((long) 2);
        language.setCode(LanguageC.RUS);

        TranslationGroup translationGroup = translationGroupDao.findTranslationGroupFor(language);

        assertNotNull(translationGroup);
        assertEquals(language.getCode(), translationGroup.getLanguage().getCode());
        assertEquals(language.getId(), translationGroup.getLanguage().getId());
        Map<String, String> translations = translationGroup.getTranslations();
        assertTrue(translations.size() >= 4);
        assertEquals("FOO сообщение", translations.get("FOO"));
        assertEquals("Эстонский язык", translations.get("Estonian"));
        assertEquals("русский язык", translations.get("Russian"));
        assertEquals("Mатематика", translations.get("TOPIC_MATHEMATICS"));
    }
}
