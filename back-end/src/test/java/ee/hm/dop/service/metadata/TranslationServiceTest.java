package ee.hm.dop.service.metadata;

import ee.hm.dop.dao.LanguageDao;
import ee.hm.dop.dao.TranslationGroupDao;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.TranslationGroup;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@RunWith(EasyMockRunner.class)
public class TranslationServiceTest {

    @TestSubject
    private TranslationService translationService = new TranslationService();

    @Mock
    private LanguageDao languageDao;

    @Mock
    private TranslationGroupDao translationGroupDao;

    @Test
    public void getTranslationsForNull() {

        replayAll();

        assertNull(translationService.getTranslationsFor(null));

        verifyAll();
    }

    @Test
    public void getTranslationsForNotSupportedLanguage() {
        String languageCode = "notSupportedLanguageCode";

        expect(languageDao.findByCode(languageCode)).andReturn(null);

        replayAll();

        assertNull(translationService.getTranslationsFor(languageCode));

        verifyAll();
    }

    @Test
    public void getTranslationsForSupportedLanguageButNoTranslation() {
        String languageCode = "supportedLanguageCode";
        Language language = createMock(Language.class);

        expect(languageDao.findByCode(languageCode)).andReturn(language);
        expect(translationGroupDao.findTranslationGroupFor(language)).andReturn(null);

        replayAll(language);

        assertNull(translationService.getTranslationsFor(languageCode));

        verifyAll(language);
    }

    @Test
    @Ignore
    public void getTranslationsForSupportedLanguageWithTranslation() {
        String languageCode = "supportedLanguageCode";
        Language language = createMock(Language.class);
        TranslationGroup translationGroup = createMock(TranslationGroup.class);
        @SuppressWarnings("unchecked")
        Map<String, String> translations = createMock(Map.class);

        expect(languageDao.findByCode(languageCode)).andReturn(language);
        expect(translationGroupDao.findTranslationGroupFor(language)).andReturn(translationGroup);
        expect(translationGroup.getTranslations()).andReturn(translations);
        expect(language.getCode()).andReturn("").times(2);

        replayAll(language, translationGroup, translations);

        assertSame(translations, translationService.getTranslationsFor(languageCode));

        verifyAll(language, translationGroup, translations);
    }

    private void replayAll(Object... mocks) {
        replay(languageDao, translationGroupDao);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(languageDao, translationGroupDao);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
