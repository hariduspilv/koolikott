package ee.hm.dop.utils;

import java.io.File;
import java.net.URL;

public class FileUtils {

    public static File getFile(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            file = null;
            URL url = FileUtils.class.getClassLoader().getResource(filePath);

            if (url != null) {
                file = new File(url.getFile());
            }
        }

        return file;
    }
}
