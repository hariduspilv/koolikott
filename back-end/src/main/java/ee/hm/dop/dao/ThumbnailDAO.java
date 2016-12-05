package ee.hm.dop.dao;


import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Size;
import ee.hm.dop.model.Thumbnail;

public class ThumbnailDAO extends BaseDAO<Thumbnail> {

        public Picture findByNameAndSize(String name, Size size) {
        return getSingleResult(createQuery("FROM Thumbnail WHERE name = :name AND size = :size", Thumbnail.class)
                .setParameter("name", name)
                .setParameter("size", size));
    }
}
