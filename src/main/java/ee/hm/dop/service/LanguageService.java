package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.LanguageDAO;
import ee.hm.dop.model.Language;

public class LanguageService {

    @Inject
    private LanguageDAO languageDAO;

    public Language getLanguage(String languageCode) {
        return languageDAO.findByCode(languageCode);
    }

}
