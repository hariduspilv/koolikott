package ee.hm.dop.service;

import ee.hm.dop.dao.PictureDAO;
import ee.hm.dop.dao.ThumbnailDAO;
import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Size;
import ee.hm.dop.model.Thumbnail;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;

public class PictureService {

    private static final int LG_XS_THUMBNAIL_WIDTH = 600;
    private static final int LG_THUMBNAIL_WIDTH = 300;

    private static final int SM_THUMBNAIL_WIDTH = 200;
    private static final int SM_THUMBNAIL_HEIGHT = 134;

    private static final int SM_XS_XL_THUMBNAIL_WIDTH = 300;
    private static final int SM_XS_XL_THUMBNAIL_HEIGHT = 200;

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

    public Thumbnail getSMThumbnailByName(String name) {
        Thumbnail thumbnail = (Thumbnail) thumbnailDAO.findByNameAndSize(name, Size.SM);

        if (thumbnail == null) {
            Picture existingPicture = getByName(name);

            if (existingPicture != null) {
                try {
                    thumbnail = createSMThumbnail(existingPicture);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return thumbnail;
    }

    public Thumbnail getSMLargeThumbnailByName(String name) {
        Thumbnail thumbnail = (Thumbnail) thumbnailDAO.findByNameAndSize(name, Size.SM_XS_XL);

        if (thumbnail == null) {
            Picture existingPicture = getByName(name);

            if (existingPicture != null) {
                try {
                    thumbnail = createSMLargeThumbnail(existingPicture);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return thumbnail;
    }

    public Thumbnail getLGLargeThumbnailByName(String name) {
        Thumbnail thumbnail = (Thumbnail) thumbnailDAO.findByNameAndSize(name, Size.LG_XS);

        if (thumbnail == null) {
            Picture existingPicture = getByName(name);

            if (existingPicture != null) {
                try {
                    thumbnail = createLGLargeThumbnail(existingPicture);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return thumbnail;
    }

    public Thumbnail getLGThumbnailByName(String name) {
        Thumbnail thumbnail = (Thumbnail) thumbnailDAO.findByNameAndSize(name, Size.LG);

        if (thumbnail == null) {
            Picture existingPicture = getByName(name);

            if (existingPicture != null) {
                try {
                    thumbnail = createLGThumbnail(existingPicture);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                newPicture = existingPicture;
            } else {
                picture.setName(name);
                newPicture = pictureDAO.update((OriginalPicture) picture);
                createThumbnails(newPicture);
            }
        }

        return newPicture;
    }

    public Picture createFromURL(String url) {

        try {
            BufferedImage image = ImageIO.read(new URL(url));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ImageIO.write(image, "jpg", baos);

            baos.flush();

            OriginalPicture result = new OriginalPicture();
            result.setData(baos.toByteArray());

            baos.close();

            return create(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createThumbnails(Picture picture) {
        try {
            createSMThumbnail(picture);
            createSMLargeThumbnail(picture);
            createLGThumbnail(picture);
            createLGLargeThumbnail(picture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Thumbnail createSMThumbnail(Picture picture) throws IOException {
        Thumbnail thumbnail = getThumbnailFromPicture(picture, SM_THUMBNAIL_WIDTH, SM_THUMBNAIL_HEIGHT);
        thumbnail.setSize(Size.SM);
        thumbnailDAO.update(thumbnail);

        return thumbnail;
    }

    private Thumbnail createSMLargeThumbnail(Picture picture) throws IOException {
        Thumbnail thumbnail = getThumbnailFromPicture(picture, SM_XS_XL_THUMBNAIL_WIDTH, SM_XS_XL_THUMBNAIL_HEIGHT);
        thumbnail.setSize(Size.SM_XS_XL);
        thumbnailDAO.update(thumbnail);

        return thumbnail;
    }

    private Thumbnail createLGLargeThumbnail(Picture picture) throws IOException {
        Thumbnail thumbnail = getThumbnailFromPicture(picture, LG_XS_THUMBNAIL_WIDTH);
        thumbnail.setSize(Size.LG_XS);
        thumbnailDAO.update(thumbnail);

        return thumbnail;
    }

    private Thumbnail createLGThumbnail(Picture picture) throws IOException {
        Thumbnail thumbnail = getThumbnailFromPicture(picture, LG_THUMBNAIL_WIDTH);
        thumbnail.setSize(Size.LG);
        thumbnailDAO.update(thumbnail);

        return thumbnail;
    }

    private String getPictureFormat(ImageInputStream imageStream) throws IOException {
        String format = DEFAULT_PICTURE_FORMAT;

        Iterator<ImageReader> readers = ImageIO.getImageReaders(imageStream);
        while (readers.hasNext()) {
            ImageReader read = readers.next();
            format = read.getFormatName().toLowerCase();
        }

        return format;
    }

    private BufferedImage resizeImage(ImageInputStream imageInputStream, final int width) throws IOException {
        BufferedImage img = ImageIO.read(imageInputStream);

        // Uploaded file is not an image
        if (img == null) {
            throw new IllegalArgumentException();
        }

        int widthToScale, heightToScale;

        // Landscape
        if (img.getWidth() > img.getHeight()) {
            heightToScale = (int) (1.1 * width);
            widthToScale = (int) ((heightToScale * 1.0) / img.getHeight()
                    * img.getWidth());
        } else {
            //Portrait
            widthToScale = (int) (1.1 * width);
            heightToScale = (int) ((widthToScale * 1.0) / img.getWidth()
                    * img.getHeight());
        }

        return Scalr.resize(img, widthToScale, heightToScale);
    }

    private BufferedImage cropThumbnailFromImage(BufferedImage image, final int width, final int height) {
        int x = (image.getWidth() - width) / 2;
        int y = (image.getHeight() - width) / 2;

        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Width of new thumbnail is bigger than original image");
        }

        return Scalr.crop(image, x, y, width, height);
    }

    private Thumbnail createThumbnail(BufferedImage image, String name, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageIO.write(image, format, baos);

        baos.flush();

        Thumbnail result = new Thumbnail();
        result.setData(baos.toByteArray());
        result.setName(name);

        baos.close();

        logger.info("Thumbnail created");
        return result;
    }

    private ImageInputStream getImageInputStreamFromPicture(byte[] imageData) throws IOException {
        return ImageIO.createImageInputStream(new ByteArrayInputStream(imageData));
    }

    private Thumbnail getThumbnailFromPicture(Picture picture, final int finalWidth) throws IOException {
        logger.info("Start creating thumbnail [name=" + picture.getName() + "]");

        ImageInputStream imageStream = getImageInputStreamFromPicture(picture.getData());
        String format = getPictureFormat(imageStream);
        BufferedImage resizedImage = resizeImage(imageStream, finalWidth);

        return createThumbnail(resizedImage, picture.getName(), format);
    }


    private Thumbnail getThumbnailFromPicture(Picture picture, final int finalWidth, final int finalHeight) throws IOException {
        logger.info("Start creating thumbnail [name=" + picture.getName() + "]");

        ImageInputStream imageStream = getImageInputStreamFromPicture(picture.getData());
        String format = getPictureFormat(imageStream);
        BufferedImage resizedImage = resizeImage(imageStream, finalWidth);
        BufferedImage thumbnailBufferedImage = cropThumbnailFromImage(resizedImage, finalWidth, finalHeight);

        return createThumbnail(thumbnailBufferedImage, picture.getName(), format);
    }
}
