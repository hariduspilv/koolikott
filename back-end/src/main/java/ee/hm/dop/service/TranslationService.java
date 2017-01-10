package ee.hm.dop.service;

import ee.hm.dop.dao.LanguageDAO;
import ee.hm.dop.dao.TranslationDAO;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.TranslationGroup;

import javax.inject.Inject;
import java.util.Map;
import java.util.stream.Collectors;

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

        if ("et".equals(languageCode.toLowerCase())) {
            return translationGroupFor.getTranslations();
        } else {
            Map<String, String> map = getDomainsAndSubjectsInEstonian();
            if (map != null) {
                translationGroupFor.getTranslations().putAll(map);
            }
            return translationGroupFor.getTranslations();
        }

    }

    /**
     * Temporary method that adds Estonian translations to eng and rus translation maps,
     * because some taxons are not yet translated into all three languages.
     * These translations are used in front-end to remove duplicate translations.
     * For example:
     *  "Matemaatika" and "Mathematics" should be same, but since some taxons don't have translations
     *  "Matemaatika" is same in all three languages. As a workaround taxons are compared in estonian.
     *
     *  This method can be removed after all taxons are translated
     * @return
     */
    private Map<String, String> getDomainsAndSubjectsInEstonian() {
        Language language = languageDAO.findByCode("et");
        if (language == null) {
            return null;
        }

        TranslationGroup translationGroupFor = translationDAO.findTranslationGroupFor(language);
        if (translationGroupFor == null) {
            return null;
        }

        return translationGroupFor.getTranslations()
                .entrySet()
                .stream()
                .filter(entry -> (entry.getKey().startsWith("DOMAIN_") || entry.getKey().startsWith("SUBJECT")))
                .collect(Collectors.toMap(entry -> "HELPER_".concat(entry.getKey()), Map.Entry::getValue));
    }

    public String getTranslationKeyByTranslation(String translation) {
        return translationDAO.getTranslationKeyByTranslation(translation);
    }

}
