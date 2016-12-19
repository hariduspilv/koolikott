package ee.hm.dop.service;

import ee.hm.dop.dao.LanguageDAO;
import ee.hm.dop.dao.TranslationDAO;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.TranslationGroup;

import javax.inject.Inject;
import java.util.Map;

public class TranslationService {

    @Inject
    private TranslationDAO translationDAO;

    @Inject
    private LanguageDAO languageDAO;

    public Map<String, String> getTranslationsFor(String languageCode) {
        if (languageCode == null) {
            return null;
        }

        Language language = languageDAO.findByCode(languageCode);
        if (language == null) {
            return null;
        }

        TranslationGroup translationGroupFor = translationDAO.findTranslationGroupFor(language);
        if (translationGroupFor == null) {
            return null;
        }

        return translationGroupFor.getTranslations();
    }

    public String getTranslationKeyByTranslation(String translation) {
        return translationDAO.getTranslationKeyByTranslation(translation);
    }

}
