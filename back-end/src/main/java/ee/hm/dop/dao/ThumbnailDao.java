package ee.hm.dop.dao;


import ee.hm.dop.model.Thumbnail;
import ee.hm.dop.model.enums.Size;

public class ThumbnailDao extends AbstractDao<Thumbnail> {

    public Thumbnail findByNameAndSize(String name, Size size) {
        return findByField("name", name, "size", size);
    }
}
