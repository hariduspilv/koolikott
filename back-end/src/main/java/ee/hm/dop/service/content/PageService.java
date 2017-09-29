package ee.hm.dop.service.content;

import javax.inject.Inject;

import ee.hm.dop.dao.PageDao;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.Page;

public class PageService {

    @Inject
    private PageDao pageDao;

    public Page getPage(String name, Language language) {
        return pageDao.findByNameAndLanguage(name, language);
    }
}
