package ee.hm.dop.dao;

import ee.hm.dop.model.Picture;

public class PictureDAO extends BaseDAO<Picture> {

    public Picture findByName(String name) {
        return getSingleResult(createQuery("FROM Picture WHERE name = :name", Picture.class) //
                .setParameter("name", name));
    }
}
