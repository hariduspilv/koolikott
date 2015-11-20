package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.LanguageDAO;
import ee.hm.dop.model.Language;

public class LanguageService {

    @Inject
    private LanguageDAO languageDAO;

    public Language getLanguage(String languageCode) {
        return languageDAO.findByCode(languageCode);
    }

    public List<Language> getAll() {
        return languageDAO.findAll();
    }

}
