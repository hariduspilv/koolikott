package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.LanguageDao;
import ee.hm.dop.model.Language;

public class LanguageService {

    @Inject
    private LanguageDao languageDao;

    public Language getLanguage(String languageCode) {
        return languageDao.findByCode(languageCode);
    }

    public List<Language> getAll() {
        return languageDao.findAll();
    }

}
