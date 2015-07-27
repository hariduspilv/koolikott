package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.TagDAO;
import ee.hm.dop.model.Tag;

public class TagService {

    @Inject
    private TagDAO tagDAO;

    public Tag getTagByName(String name) {
        return tagDAO.findTagByName(name);
    }
}
