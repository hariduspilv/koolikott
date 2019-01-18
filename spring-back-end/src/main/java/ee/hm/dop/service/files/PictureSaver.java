package ee.hm.dop.service.files;

import ee.hm.dop.dao.OriginalPictureDao;
import ee.hm.dop.dao.ThumbnailDao;
import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Thumbnail;
import ee.hm.dop.model.enums.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;

@Service
public class PictureSaver {

    public static final String DEFAULT_PICTURE_FORMAT = "jpg";
    public Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private OriginalPictureDao originalPictureDao;
    @Inject
    private ThumbnailDao thumbnailDao;

    public Thumbnail createOneThumbnail(Picture existingPicture, Size size) {
        return tryToCreateThumbnail(existingPicture, size);
    }

    public Picture create(Picture picture) {
        if (picture.getId() != null) {
            throw new WebApplicationException("Picture already exists", Response.Status.BAD_REQUEST);
        }
        String name = sha1Hex(picture.getData());
        picture.setName(name);
        Picture existingPicture = originalPictureDao.findByNameAny(name);
        //pictures are different, we always save new, but no need to cut thumbnails
        Picture newPicture = originalPictureDao.createOrUpdate((OriginalPicture) picture);
        if (existingPicture == null) {
            createAllThumbnails(newPicture);
        }
        return newPicture;
    }

    public Picture createFromURL(String url) {
        try {
            BufferedImage image = ImageIO.read(new URL(url));
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
                ImageIO.write(image, DEFAULT_PICTURE_FORMAT, baos);
                baos.flush();

                OriginalPicture result = new OriginalPicture();
                result.setData(baos.toByteArray());
                return create(result);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    private Thumbnail tryToCreateThumbnail(Picture existingPicture, Size sm) {
        try {
            Thumbnail thumbnail = PictureCutter.getThumbnailFromPicture(existingPicture, sm);
            return thumbnailDao.createOrUpdate(thumbnail);
        } catch (IOException ignored) {
            logError(existingPicture);
            return null;
        }
    }

    private void createAllThumbnails(Picture picture) {
        for (Size size : Size.values()) {
            tryToCreateThumbnail(picture, size);
        }
    }

    private void logError(Picture existingPicture) {
        logger.error("creating picture failed id: " + existingPicture.getId());
    }

}