package ee.hm.dop.service;

import java.util.Map;

import javax.inject.Inject;

import ee.hm.dop.dao.LanguageDAO;
import ee.hm.dop.dao.TranslationDAO;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.TranslationGroup;

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

}
