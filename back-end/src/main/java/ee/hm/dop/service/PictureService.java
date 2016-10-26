package ee.hm.dop.service;

import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.inject.Inject;

import ee.hm.dop.dao.PictureDAO;
import ee.hm.dop.dao.ThumbnailDAO;
import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Thumbnail;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class PictureService {

    private static final int THUMBNAIL_WIDTH = 200;
    private static final String DEFAULT_PICTURE_FORMAT = "jpg";
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private PictureDAO pictureDAO;

    @Inject
    private ThumbnailDAO thumbnailDAO;

    private static final Object lock = new Object();


    public Picture getByName(String name) {
        return pictureDAO.findByName(name);
    }


    public Picture getThumbnailByName(String name) {
        Picture thumbnail = thumbnailDAO.findByName(name);

        if (thumbnail == null) {
            Picture existingPicture = getByName(name);

            if (existingPicture != null) {
                thumbnail = createThumbnail(existingPicture);
            }
        }

        return thumbnail;
    }


    public Picture create(Picture picture) {
        if (picture.getId() != null) {
            throw new RuntimeException("Picture already exists");
        }

        String name = sha1Hex(picture.getData());

        Picture newPicture;

        synchronized (lock) {
            Picture existingPicture = getByName(name);

            if (existingPicture != null) {
                Picture existingThumbnail = getThumbnailByName(name);

                if (existingThumbnail == null) {
                    createThumbnail(existingPicture);
                }

                newPicture = existingPicture;
            } else {
                picture.setName(name);
                newPicture = pictureDAO.update((OriginalPicture) picture);
                createThumbnail(newPicture);
            }
        }

        return newPicture;
    }


    Picture createThumbnail(Picture picture) {
        Picture thumbnail = getThumbnailFromPicture(picture);
        thumbnailDAO.update((Thumbnail) thumbnail);
        return thumbnail;
    }


    private String getPictureFormat(Picture picture) {

        String format = DEFAULT_PICTURE_FORMAT;

        try {
            ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(picture.getData()));

            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            while (readers.hasNext()) {
                ImageReader read = readers.next();
                format = read.getFormatName().toLowerCase();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return format;
    }


    Picture getThumbnailFromPicture(Picture picture) {

        logger.info("Start downscaleing picture [name=" + picture.getName() + "]");

        Picture result = null;
        String format = getPictureFormat(picture);

        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(picture.getData()));

            // Uploaded file is not an image
            if (img == null) {
                throw new IllegalArgumentException();
            }

            int widthToScale, heightToScale;

            // Landscape
            if (img.getWidth() > img.getHeight()) {
                heightToScale = (int)(1.1 * THUMBNAIL_WIDTH);
                widthToScale = (int)((heightToScale * 1.0) / img.getHeight()
                        * img.getWidth());
            } else {
                //Portrait
                widthToScale = (int)(1.1 * THUMBNAIL_WIDTH);
                heightToScale = (int)((widthToScale * 1.0) / img.getWidth()
                        * img.getHeight());
            }

            BufferedImage resizedImage = Scalr.resize(img, widthToScale, heightToScale);

            int x = (resizedImage.getWidth() - THUMBNAIL_WIDTH) / 2;
            int y = (resizedImage.getHeight() - THUMBNAIL_WIDTH) / 2;

            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("Width of new thumbnail is bigger than original image");
            }

            BufferedImage thumbnailBufferedImage = Scalr.crop(resizedImage, x, y, THUMBNAIL_WIDTH, THUMBNAIL_WIDTH);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnailBufferedImage, format, baos);
            baos.flush();

            result = new Thumbnail();
            result.setData(baos.toByteArray());
            baos.close();

            result.setName(picture.getName());

            logger.info("Downscaleing successful");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
