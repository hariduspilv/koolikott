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

    @Inject
    private OriginalPictureDao originalPictureDao;
    @Inject
    private ThumbnailDao thumbnailDao;

    public Thumbnail tryToCreateSM(Picture existingPicture) {
        return createThumbnail(existingPicture, Size.SM, SM_THUMBNAIL_WIDTH, SM_THUMBNAIL_HEIGHT);
    }

    public Thumbnail tryToCreateSMLarge(Picture existingPicture) {
        return createThumbnail(existingPicture, Size.SM_XS_XL, SM_XS_XL_THUMBNAIL_WIDTH, SM_XS_XL_THUMBNAIL_HEIGHT);
    }

    public Thumbnail tryToCreateLG(Picture existingPicture) {
        return createThumbnail(existingPicture, Size.LG, LG_THUMBNAIL_WIDTH, null);
    }

    public Thumbnail tryToCreateLGLarge(Picture existingPicture) {
        return createThumbnail(existingPicture, Size.LG_XS, LG_XS_THUMBNAIL_WIDTH, null);
    }

    public Picture create(Picture picture) {
        if (picture.getId() != null) {
            throw new RuntimeException("Picture already exists");
        }
        String name = sha1Hex(picture.getData());
        picture.setName(name);
        Picture existingPicture = originalPictureDao.findByNameAny(name);
        //pictures are different, we always save new, but no need to cut thumbnails
        Picture newPicture = originalPictureDao.createOrUpdate((OriginalPicture) picture);
        if (existingPicture == null) {
            createThumbnails(newPicture);
        }
        return newPicture;
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

    private Thumbnail createThumbnail(Picture existingPicture, Size sm, Integer smThumbnailWidth, Integer smThumbnailHeight) {
        try {
            return createThumbnailInner(existingPicture, sm, smThumbnailWidth, smThumbnailHeight);
        } catch (IOException ignored) {
            logError(existingPicture);
            return null;
        }
    }

    private void createThumbnails(Picture picture) {
        try {
            createThumbnailInner(picture, Size.SM, SM_THUMBNAIL_WIDTH, SM_THUMBNAIL_HEIGHT);
            createThumbnailInner(picture, Size.SM_XS_XL, SM_XS_XL_THUMBNAIL_WIDTH, SM_XS_XL_THUMBNAIL_HEIGHT);
            createThumbnailInner(picture, Size.LG, LG_THUMBNAIL_WIDTH, null);
            createThumbnailInner(picture, Size.LG_XS, LG_XS_THUMBNAIL_WIDTH, null);
        } catch (IOException ignored) {
            logError(picture);
        }
    }

    private Thumbnail createThumbnailInner(Picture picture, Size size, int width, Integer height) throws IOException {
        Thumbnail thumbnail;
        if (height == null){
            thumbnail = PictureCutter.getThumbnailFromPicture(picture, width);
        } else {
            thumbnail = PictureCutter.getThumbnailFromPicture(picture, width, height);
        }
        thumbnail.setSize(size);
        thumbnailDao.createOrUpdate(thumbnail);
        return thumbnail;
    }

    private void logError(Picture existingPicture) {
        logger.error("creating picture failed id: " + existingPicture.getId());
    }

}