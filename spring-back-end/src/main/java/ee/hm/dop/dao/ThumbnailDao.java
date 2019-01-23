package ee.hm.dop.dao;


import ee.hm.dop.model.Thumbnail;
import ee.hm.dop.model.enums.Size;
import org.springframework.stereotype.Repository;

@Repository
public class ThumbnailDao extends AbstractDao<Thumbnail> {

    public Thumbnail findByNameAndSize(String name, Size size) {
        return findByFieldAny("name", name, "size", size);
    }
}
