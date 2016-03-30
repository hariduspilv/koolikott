package ee.hm.dop.service;

import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;

import javax.inject.Inject;

import ee.hm.dop.dao.PictureDAO;
import ee.hm.dop.model.Picture;

public class PictureService {

    @Inject
    private PictureDAO pictureDAO;

    private static final Object lock = new Object();

    public Picture getByName(String name) {
        return pictureDAO.findByName(name);
    }

    public Picture create(Picture picture) {
        if (picture.getId() != null) {
            throw new RuntimeException("Picture already exists");
        }

        String name = sha1Hex(picture.getData());

        Picture newPicture = null;
        synchronized (lock) {
            Picture existingPicture = getByName(name);
            if (existingPicture != null) {
                newPicture = existingPicture;
            } else {
                picture.setName(name);
                newPicture = pictureDAO.update(picture);
            }
        }

        return newPicture;
    }
}
