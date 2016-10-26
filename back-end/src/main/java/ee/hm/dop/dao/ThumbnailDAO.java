package ee.hm.dop.dao;


import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Thumbnail;

public class ThumbnailDAO extends BaseDAO<Thumbnail> {

        public Picture findByName(String name) {
        return getSingleResult(createQuery("FROM Thumbnail WHERE name = :name", Thumbnail.class)
                .setParameter("name", name));
    }
}
