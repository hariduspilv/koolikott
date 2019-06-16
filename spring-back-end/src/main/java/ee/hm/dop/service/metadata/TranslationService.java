package ee.hm.dop.service.metadata;

import ee.hm.dop.dao.LanguageDao;
import ee.hm.dop.dao.TranslationGroupDao;
import ee.hm.dop.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TranslationService {

    public static final String LANDING_PAGE_DESCRIPTION = "LANDING_PAGE_DESCRIPTION";
    public static final String LANDING_PAGE_NOTICE = "LANDING_PAGE_NOTICE";
    @Inject
    private TranslationGroupDao translationGroupDao;
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
        TranslationGroup translationGroupFor = translationGroupDao.findTranslationGroupFor(language);
        if (translationGroupFor == null) {
            return null;
        }
        return translationGroupFor.getTranslations();
    }

    public String getTranslationKeyByTranslation(String translation) {
        return translationGroupDao.getTranslationKeyByTranslation(translation);
    }

    public static LanguageString filterByLanguage(List<LanguageString> languageStringList, String lang) {
        return languageStringList.stream()
                .filter(languageString -> languageString.getLanguage().getCode().equals(lang))
                .findAny()
                .orElse(null);
    }

    public void update(LandingPageObject landingPage) {
        landingPage.getDescriptions().forEach(d -> updateText(d, LANDING_PAGE_DESCRIPTION));
        landingPage.getNotices().forEach(n -> updateText(n, LANDING_PAGE_NOTICE));
    }

    public void updateText(LandingPageString pageString, String landingPageDescription) {
        translationGroupDao.updateTranslation(pageString.getText(), landingPageDescription, pageString.getLanguage());
    }

    public LandingPageObject getTranslations() {
        return new LandingPageObject(list(LANDING_PAGE_NOTICE), list(LANDING_PAGE_DESCRIPTION));
    }

    public List<LandingPageString> list(String key) {
        return translationGroupDao.getTranslations(key);
    }

    public void updateTranslation(TranslationObject translationObject) {
        translationGroupDao.updateTranslation(translationObject.getTranslation(), translationObject.getTranslationKey(), translationObject.transformLanguageKey());
    }

    public TranslationObject getTranslations(TranslationObject translationObject) {
        return translationGroupDao.getTranslationByKeyAndLangcode(translationObject.getTranslationWithId(), translationObject.getTranslationKey());
    }
}
