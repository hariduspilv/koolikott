package ee.hm.dop.service;

import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;

import javax.inject.Inject;

import ee.hm.dop.dao.PictureDAO;
import ee.hm.dop.model.Picture;

public class PictureService {

    @Inject
    private PictureDAO pictureDAO;

    public Picture getByName(String name) {
        return pictureDAO.findByName(name);
    }

    public Picture create(Picture picture) {
        if (picture.getId() != null) {
            throw new RuntimeException("Picture already exists");
        }

        String name = sha1Hex(picture.getData());

        Picture existingPicture = getByName(name);
        if (existingPicture != null) {
            return existingPicture;
        }

        picture.setName(name);
        return pictureDAO.update(picture);
    }
}
