package ee.hm.dop.utils;

import static java.lang.String.format;

import java.io.File;
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
}
