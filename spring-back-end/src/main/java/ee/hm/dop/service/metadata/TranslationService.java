package ee.hm.dop.service.metadata;

import ee.hm.dop.dao.LanguageDao;
import ee.hm.dop.dao.TranslationGroupDao;
import ee.hm.dop.model.LandingPageObject;
import ee.hm.dop.model.LandingPageString;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.TranslationGroup;
import ee.hm.dop.model.TranslationsDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collections;
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

    public void updateTranslation(TranslationsDto translationsDto) {
        translationGroupDao.updateTranslation(translationsDto.getTranslations().get(0),
                translationsDto.getTranslationKey(),
                languageDao.findCodeByCode(translationsDto.getLanguageKeys().get(0)));
    }

    public void updateTranslations(TranslationsDto translationsDtos) {
        for (int i = 0; i < translationsDtos.getTranslations().size(); i++) {
            translationGroupDao.updateTranslation(
                    translationsDtos.getTranslations().get(i),
                    translationsDtos.getTranslationKey(),
                    languageDao.findCodeByCode(translationsDtos.getLanguageKeys().get(i)));
        }
    }

    public String getTranslations(String translationKey, String languageCode) {
        return translationGroupDao.getTranslationByKeyAndLangcode(translationKey, languageDao.findIdByCode(languageCode));
    }

    public List<String> getAllTranslations(String translationKey) {
        return translationGroupDao.getTranslationsForKey(Collections.singletonList(translationKey));
    }
}
