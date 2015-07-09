package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.PageDAO;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.Page;

public class PageService {

    @Inject
    PageDAO pageDao;

    public Page getPage(String name, Language language) {
        if (name == null || language == null) {
            return null;
        }
        return pageDao.findByNameAndLang(name, language);
    }
}
