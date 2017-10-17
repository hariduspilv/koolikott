package ee.hm.dop.service.files;

import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Thumbnail;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class PictureCutter {
    private static Logger logger = LoggerFactory.getLogger(PictureCutter.class);

    public static Thumbnail getThumbnailFromPicture(Picture picture, final int finalWidth) throws IOException {
        logger.info("Start creating thumbnail [name=" + picture.getName() + "]");

        ImageInputStream imageStream = getImageInputStreamFromPicture(picture.getData());
        String format = getPictureFormat(imageStream);
        BufferedImage resizedImage = resizeImage(imageStream, finalWidth);

        return createThumbnail(resizedImage, picture.getName(), format);
    }


    public static Thumbnail getThumbnailFromPicture(Picture picture, final int finalWidth, final int finalHeight) throws IOException {
        logger.info("Start creating thumbnail [name=" + picture.getName() + "]");

        ImageInputStream imageStream = getImageInputStreamFromPicture(picture.getData());
        String format = getPictureFormat(imageStream);
        BufferedImage resizedImage = resizeImage(imageStream, finalWidth);
        BufferedImage thumbnailBufferedImage = cropThumbnailFromImage(resizedImage, finalWidth, finalHeight);

        return createThumbnail(thumbnailBufferedImage, picture.getName(), format);
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

    private static BufferedImage cropThumbnailFromImage(BufferedImage image, final int width, final int height) {
        int x = (image.getWidth() - width) / 2;
        int y = (image.getHeight() - width) / 2;

        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Width of new thumbnail is bigger than original image");
        }

        return Scalr.crop(image, x, y, width, height);
    }

    private static Thumbnail createThumbnail(BufferedImage image, String name, String format) throws IOException {
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

    private static ImageInputStream getImageInputStreamFromPicture(byte[] imageData) throws IOException {
        return ImageIO.createImageInputStream(new ByteArrayInputStream(imageData));
    }
}