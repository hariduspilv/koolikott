package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.LanguageDAO;
import ee.hm.dop.dao.PageDAO;
import ee.hm.dop.model.Page;

public class PageService {

    @Inject
    PageDAO pageDao;

    @Inject
    LanguageDAO languageDao;

    public Page get(String pageName, String languageCode) {
        return pageDao.findByNameAndLang(pageName, languageDao.findByCode(languageCode));
    }
}
