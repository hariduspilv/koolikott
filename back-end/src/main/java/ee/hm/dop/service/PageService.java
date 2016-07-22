package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.PageDAO;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.Page;

public class PageService {

    @Inject
    private PageDAO pageDao;

    public Page getPage(String name, Language language) {
        return pageDao.findByNameAndLanguage(name, language);
    }
}
