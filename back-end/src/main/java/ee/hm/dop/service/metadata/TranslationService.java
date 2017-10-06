package ee.hm.dop.service.metadata;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ee.hm.dop.dao.LanguageDao;
import ee.hm.dop.dao.TranslationDAO;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.TranslationGroup;
import ee.hm.dop.model.enums.LanguageC;

public class TranslationService {

    public static final String DOMAIN = "DOMAIN_";
    public static final String SUBJECT = "SUBJECT";
    public static final String HELPER = "HELPER_";
    @Inject
    private TranslationDAO translationDAO;
    @Inject
    private LanguageDao languageDao;

    public Map<String, String> getTranslationsFor(String languageCode) {
        if (languageCode == null) {
            return null;
        }

        Language language = languageDao.findByCode(languageCode);
        if (language == null) {
            return null;
        }
        TranslationGroup translationGroupFor = translationDAO.findTranslationGroupFor(language);
        if (translationGroupFor == null) {
            return null;
        }

        if (LanguageC.ENG.equals(language.getCode()) || LanguageC.RUS.equals(language.getCode())) {
            Map<String, String> map = getDomainsAndSubjectsInEstonian();
            if (map != null) {
                map.putAll(translationGroupFor.getTranslations());
                return map;
            }
        }

        return translationGroupFor.getTranslations();
    }

    /**
     * Temporary method that adds Estonian translations to eng and rus translation maps,
     * because some taxons are not yet translated into all three languages.
     * These translations are used in front-end to remove duplicate translations.
     * For example:
     * "Matemaatika" and "Mathematics" should be same, but since some taxons don't have translations
     * "Matemaatika" is same in all three languages. As a workaround taxons are compared in estonian.
     * <p>
     * This method can be removed after all taxons are translated
     *
     * @return
     */
    private Map<String, String> getDomainsAndSubjectsInEstonian() {
        Language language = languageDao.findByCode(LanguageC.EST);
        if (language == null) {
            return null;
        }
        TranslationGroup translationGroupFor = translationDAO.findTranslationGroupFor(language);
        if (translationGroupFor == null) {
            return null;
        }
        return translationGroupFor.getTranslations().entrySet().stream()
                .filter(entry -> (entry.getKey().startsWith(DOMAIN) || entry.getKey().startsWith(SUBJECT)))
                .collect(Collectors.toMap(entry -> HELPER.concat(entry.getKey()), Map.Entry::getValue));
    }

    public String getTranslationKeyByTranslation(String translation) {
        return translationDAO.getTranslationKeyByTranslation(translation);
    }

    public static LanguageString filterByLanguage(List<LanguageString> languageStringList, String lang) {
        return languageStringList.stream()
                .filter(languageString -> languageString.getLanguage().getCode().equals(lang))
                .findAny()
                .orElse(null);
    }
}
