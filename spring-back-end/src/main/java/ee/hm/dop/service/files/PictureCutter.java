package ee.hm.dop.service.files;

import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Thumbnail;
import ee.hm.dop.model.enums.Size;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Service
@Transactional
public class PictureCutter {
    public static final Integer LG_XS_THUMBNAIL_WIDTH = 600;
    public static final Integer LG_THUMBNAIL_WIDTH = 300;
    public static final Integer SM_THUMBNAIL_WIDTH = 200;
    public static final Integer SM_THUMBNAIL_HEIGHT = 134;
    public static final Integer SM_XS_XL_THUMBNAIL_WIDTH = 300;
    public static final Integer SM_XS_XL_THUMBNAIL_HEIGHT = 200;
    private static Logger logger = LoggerFactory.getLogger(PictureCutter.class);

    public static Thumbnail getThumbnailFromPicture(Picture picture, Size size) throws IOException {
        if (size.createUsingWidthAndHeight()) {
            return getThumbnailFromPicture(picture, size, size.getWidth(), size.getHeight());
        }
        return getThumbnailFromPicture(picture, size, size.getWidth());
    }

    public static Thumbnail getThumbnailFromPicture(Picture picture, Size size, final int finalWidth) throws IOException {
        logger.info("Start creating thumbnail [name=" + picture.getName() + "]");

        ImageInputStream imageStream = getImageInputStreamFromPicture(picture.getData());
        String format = getPictureFormat(imageStream);
        BufferedImage resizedImage = resizeImage(imageStream, finalWidth);

        return createThumbnail(resizedImage, size, picture.getName(), format);
    }


    public static Thumbnail getThumbnailFromPicture(Picture picture, Size size, final int finalWidth, final int finalHeight) throws IOException {
        logger.info("Start creating thumbnail [name=" + picture.getName() + "]");

        ImageInputStream imageStream = getImageInputStreamFromPicture(picture.getData());
        String format = getPictureFormat(imageStream);
        BufferedImage resizedImage = resizeImage(imageStream, finalWidth);
        BufferedImage thumbnailBufferedImage = cropThumbnailFromImage(resizedImage, finalWidth, finalHeight);

        return createThumbnail(thumbnailBufferedImage, size, picture.getName(), format);
    }

    private static String getPictureFormat(ImageInputStream imageStream) throws IOException {
        String format = PictureSaver.DEFAULT_PICTURE_FORMAT;

        Iterator<ImageReader> readers = ImageIO.getImageReaders(imageStream);
        while (readers.hasNext()) {
            ImageReader read = readers.next();
            format = read.getFormatName().toLowerCase();
        }

        return format;
    }

    private static BufferedImage resizeImage(ImageInputStream imageInputStream, final int width) throws IOException {
        BufferedImage img = ImageIO.read(imageInputStream);

        // Uploaded file is not an image
        if (img == null) {
            throw new IllegalArgumentException();
        }

        int widthToScale, heightToScale;

        // Landscape
        if (img.getWidth() > img.getHeight()) {
            heightToScale = (int) (1.1 * width);
            widthToScale = (int) ((heightToScale * 1.0) / img.getHeight() * img.getWidth());
        } else {
            //Portrait
            widthToScale = (int) (1.1 * width);
            heightToScale = (int) ((widthToScale * 1.0) / img.getWidth() * img.getHeight());
        }

        return Scalr.resize(img, widthToScale, heightToScale);
    }

    private static BufferedImage cropThumbnailFromImage(BufferedImage image, final int width, final int height) {
        int x = (image.getWidth() - width) / 2;
        int y = (image.getHeight() - width) / 2;

        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Width of new thumbnail is bigger than original image");
        }

        return Scalr.crop(image, x, y, width, height);
    }

    private static Thumbnail createThumbnail(BufferedImage image, Size size, String name, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageIO.write(image, format, baos);

        baos.flush();

        Thumbnail result = new Thumbnail();
        result.setData(baos.toByteArray());
        result.setName(name);
        result.setSize(size);

        baos.close();

        logger.info("Thumbnail created");
        return result;
    }

    private static ImageInputStream getImageInputStreamFromPicture(byte[] imageData) throws IOException {
        return ImageIO.createImageInputStream(new ByteArrayInputStream(imageData));
    }
}