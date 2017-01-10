package ee.hm.dop.service;

import ee.hm.dop.dao.LanguageDAO;
import ee.hm.dop.dao.TranslationDAO;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.TranslationGroup;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@RunWith(EasyMockRunner.class)
public class TranslationServiceTest {

    @TestSubject
    private TranslationService translationService = new TranslationService();

    @Mock
    private LanguageDAO languageDAO;

    @Mock
    private TranslationDAO translationDAO;

    @Test
    public void getTranslationsForNull() {

        replayAll();

        assertNull(translationService.getTranslationsFor(null));

        verifyAll();
    }

    @Test
    public void getTranslationsForNotSupportedLanguage() {
        String languageCode = "notSupportedLanguageCode";

        expect(languageDAO.findByCode(languageCode)).andReturn(null);

        replayAll();

        assertNull(translationService.getTranslationsFor(languageCode));

        verifyAll();
    }

    @Test
    public void getTranslationsForSupportedLanguageButNoTranslation() {
        String languageCode = "supportedLanguageCode";
        Language language = createMock(Language.class);

        expect(languageDAO.findByCode(languageCode)).andReturn(language);
        expect(translationDAO.findTranslationGroupFor(language)).andReturn(null);

        replayAll(language);

        assertNull(translationService.getTranslationsFor(languageCode));

        verifyAll(language);
    }

    @Test
    public void getTranslationsForSupportedLanguageWithTranslation() {
        String languageCode = "supportedLanguageCode";
        Language language = createMock(Language.class);
        TranslationGroup translationGroup = createMock(TranslationGroup.class);
        @SuppressWarnings("unchecked")
        Map<String, String> translations = createMock(Map.class);

        expect(languageDAO.findByCode(languageCode)).andReturn(language);
        expect(translationDAO.findTranslationGroupFor(language)).andReturn(translationGroup).times(2);
        expect(translationGroup.getTranslations()).andReturn(translations).times(3);
        expect(languageDAO.findByCode("et")).andReturn(language);
        expect(translations.entrySet()).andReturn(new HashSet<>());

        translations.putAll(new HashMap<>());
        EasyMock.expectLastCall();

        replayAll(language, translationGroup, translations);

        assertSame(translations, translationService.getTranslationsFor(languageCode));

        verifyAll(language, translationGroup, translations);
    }

    private void replayAll(Object... mocks) {
        replay(languageDAO, translationDAO);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(languageDAO, translationDAO);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
