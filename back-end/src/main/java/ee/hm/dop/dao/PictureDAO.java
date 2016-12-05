package ee.hm.dop.dao;

import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Picture;

public class PictureDAO extends BaseDAO<OriginalPicture> {

    public Picture findByName(String name) {
        return getSingleResult(createQuery("FROM OriginalPicture WHERE name = :name", OriginalPicture.class) //
                .setParameter("name", name));
    }
}
