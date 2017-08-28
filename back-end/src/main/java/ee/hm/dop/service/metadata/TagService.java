package ee.hm.dop.service.metadata;

import javax.inject.Inject;

import ee.hm.dop.dao.TagDao;
import ee.hm.dop.model.Tag;

public class TagService {

    @Inject
    private TagDao tagDao;

    public Tag getTagByName(String name) {
        return tagDao.findByName(name);
    }
}
