package ee.hm.dop.utils;

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);


    public static File getFile(String filePath) {
        logger.info(format("Getting file from %s", filePath));
        File file = new File(filePath);

        if (!file.exists()) {
            file = null;
            URL url = FileUtils.class.getClassLoader().getResource(filePath);

            if (url != null) {
                file = new File(url.getFile());
            }
        }

        logger.info("Returning " + file);

        return file;
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

        return FileUtils.class.getClassLoader().getResourceAsStream(filePath);
    }
}
