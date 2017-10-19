package ee.hm.dop.utils;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ee.hm.dop.model.UploadedFile;
import ee.hm.dop.service.files.ZipService;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

public class DOPFileUtils {
    private static final Logger logger = LoggerFactory.getLogger(DOPFileUtils.class);
    public static final int MB_TO_B_MULTIPLIER = 1024 * 1024;


    public static String cleanFileName(String fileName) {
        return fileName.replaceAll(" ", "+");
    }

    public static String probeForMediaType(String filename) {
        try {
            return Files.probeContentType(Paths.get(filename.toLowerCase()));
        } catch (IOException e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public static String encode(String filenameCleaned) throws UnsupportedEncodingException {
        return URLEncoder.encode(filenameCleaned, UTF_8.name());
    }

    public static String getRealFilename(String filename, UploadedFile file) {
        if (filename.contains("/")) {
            return file.getName() + filename.substring(filename.indexOf("/"), filename.length());
        }
        return file.getName();
    }

    public static InputStream getFileAsStream(String filePath) {
        logger.info(format("Getting file from %s", filePath));
        File file = new File(filePath);

        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        logger.info(format("File %s does not exist. Trying to load from classpath.", filePath));
        InputStream inputStream = DOPFileUtils.class.getClassLoader().getResourceAsStream(filePath);
        if (inputStream == null) logger.info(format("File %s could not be loaded from classpath", file.getName()));

        return inputStream;
    }

    public static File getFile(String filePath) {
        logger.info(format("Getting file from %s", filePath));
        File file = new File(filePath);

        if (!file.exists()) {
            logger.info(format("File %s does not exist. Trying to load from classpath.", filePath));

            URL resource = DOPFileUtils.class.getClassLoader().getResource(filePath);
            if (resource != null) {
                try {
                    file = new File(resource.toURI());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return file;
    }

    public static String readFileAsString(String filePath) {
        InputStream in = null;
        try {
            in = getFileAsStream(filePath);
            return IOUtils.toString(in, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Read the inputStream till the maxSize. Throws an Exception if file is
     * bigger than maxSize.
     *
     * @param inputStream the file to be read
     * @param maxSize     the max size in MB.
     * @return the read file as bytes
     */
    public static byte[] read(InputStream inputStream, int maxSize) {
        // Size is in MB
        int maxFileSize = maxSize * MB_TO_B_MULTIPLIER;
        byte[] bytes = new byte[maxFileSize + 1];

        int read;
        try {
            read = IOUtils.read(inputStream, bytes);
            if (read > maxFileSize) {
                throw new RuntimeException("File bigger than max size allowed");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Arrays.copyOf(bytes, read);
    }

    public static void writeToFile(InputStream inputStream, String location) {
        try {
            File targetFile = new File(location);
            targetFile.getParentFile().mkdirs();
            FileUtils.copyInputStreamToFile(inputStream, targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("Unable to write file: " + location);
        }

    }

    public static void unpackArchive(InputStream inputStream, String location) {
        byte[] buffer = new byte[ZipService.COMPRESSION_MEMORY];
        ZipInputStream zis = new ZipInputStream(inputStream);
        try {
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(location + File.separator + fileName);

                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            throw new RuntimeException("File is not a subtype of an ZIP archive");
        }
    }
}
