package ee.hm.dop.service.files;

import ee.hm.dop.dao.OriginalPictureDao;
import ee.hm.dop.dao.ThumbnailDao;
import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Thumbnail;
import ee.hm.dop.model.enums.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;

public class PictureSaver {

    public static final int LG_XS_THUMBNAIL_WIDTH = 600;
    public static final int LG_THUMBNAIL_WIDTH = 300;
    public static final int SM_THUMBNAIL_WIDTH = 200;
    public static final int SM_THUMBNAIL_HEIGHT = 134;
    public static final int SM_XS_XL_THUMBNAIL_WIDTH = 300;
    public static final int SM_XS_XL_THUMBNAIL_HEIGHT = 200;
    public static final String DEFAULT_PICTURE_FORMAT = "jpg";
    public Logger logger = LoggerFactory.getLogger(getClass());

    private static final Object lock = new Object();
    @Inject
    private OriginalPictureDao originalPictureDao;
    @Inject
    private ThumbnailDao thumbnailDao;

    public Thumbnail tryToCreateSM(Picture existingPicture) {
        try {
            return createSMThumbnail(existingPicture);
        } catch (IOException ignored) {
            logError(existingPicture);
            return null;
        }
    }

    public Thumbnail tryToCreateSMLarge(Picture existingPicture) {
        try {
            return createSMLargeThumbnail(existingPicture);
        } catch (IOException ignored) {
            logError(existingPicture);
            return null;
        }
    }

    public Thumbnail tryToCreateLG(Picture existingPicture) {
        try {
            return createLGThumbnail(existingPicture);
        } catch (IOException ignored) {
            logError(existingPicture);
            return null;
        }
    }

    public Thumbnail tryToCreateLGLarge(Picture existingPicture) {
        try {
            return createLGLargeThumbnail(existingPicture);
        } catch (IOException ignored) {
            logError(existingPicture);
            return null;
        }
    }

    public Picture create(Picture picture) {
        if (picture.getId() != null) {
            throw new RuntimeException("Picture already exists");
        }
        String name = sha1Hex(picture.getData());
        synchronized (lock) {
            Picture existingPicture = originalPictureDao.findByName(name);

            if (existingPicture != null) {
                return existingPicture;
            }
            picture.setName(name);
            Picture newPicture = originalPictureDao.createOrUpdate((OriginalPicture) picture);
            createThumbnails(newPicture);
            return newPicture;
        }
    }

    public Picture createFromURL(String url) {
        try {
            BufferedImage image = ImageIO.read(new URL(url));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, DEFAULT_PICTURE_FORMAT, baos);
            baos.flush();

            OriginalPicture result = new OriginalPicture();
            result.setData(baos.toByteArray());

            baos.close();
            return create(result);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    private void createThumbnails(Picture picture) {
        try {
            createSMThumbnail(picture);
            createSMLargeThumbnail(picture);
            createLGThumbnail(picture);
            createLGLargeThumbnail(picture);
        } catch (IOException ignored) {
            logError(picture);
        }
    }

    private Thumbnail createSMThumbnail(Picture picture) throws IOException {
        Thumbnail thumbnail = PictureCutter.getThumbnailFromPicture(picture, SM_THUMBNAIL_WIDTH, SM_THUMBNAIL_HEIGHT);
        thumbnail.setSize(Size.SM);
        thumbnailDao.createOrUpdate(thumbnail);
        return thumbnail;
    }

    private Thumbnail createSMLargeThumbnail(Picture picture) throws IOException {
        Thumbnail thumbnail = PictureCutter.getThumbnailFromPicture(picture, SM_XS_XL_THUMBNAIL_WIDTH, SM_XS_XL_THUMBNAIL_HEIGHT);
        thumbnail.setSize(Size.SM_XS_XL);
        thumbnailDao.createOrUpdate(thumbnail);
        return thumbnail;
    }

    private Thumbnail createLGLargeThumbnail(Picture picture) throws IOException {
        Thumbnail thumbnail = PictureCutter.getThumbnailFromPicture(picture, LG_XS_THUMBNAIL_WIDTH);
        thumbnail.setSize(Size.LG_XS);
        thumbnailDao.createOrUpdate(thumbnail);
        return thumbnail;
    }

    private Thumbnail createLGThumbnail(Picture picture) throws IOException {
        Thumbnail thumbnail = PictureCutter.getThumbnailFromPicture(picture, LG_THUMBNAIL_WIDTH);
        thumbnail.setSize(Size.LG);
        thumbnailDao.createOrUpdate(thumbnail);
        return thumbnail;
    }

    private void logError(Picture existingPicture) {
        logger.error("creating picture failed id: " + existingPicture.getId());
    }

}