package ee.hm.dop.dao;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.Page;

public class PageDao extends AbstractDao<Page>{

    public Page findByNameAndLanguage(String name, Language language) {
        return findByField("name", name, "language", language);
    }
}
