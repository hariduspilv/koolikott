package ee.hm.dop.utils.io;

import ee.hm.dop.service.reviewmanagement.dto.FileFormat;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class CsvUtil {

    public static final String TEMP_FOLDER = System.getProperty("java.io.tmpdir") + File.separator + "ekoolikott" + File.separator;

    public static String getUniqueFileName(FileFormat format) {
//        File tempFolder = new File(TEMP_FOLDER);
        File tempFolder = new File("tmp");
        boolean tempFolderExists = true;
        if (!tempFolder.exists()) {
            tempFolderExists = tempFolder.mkdir();
        }
        if (tempFolderExists) {
            String child = String.valueOf(new Date().getTime()) + UUID.randomUUID() + "." + format.name();
            return new File(tempFolder, child).getAbsolutePath();
        }
        throw new RuntimeException("Temporary folder does not exist!");
    }

    public static String getExcelNumber(String number) {
        return number != null ? number.replaceAll("\\.", ",") : "";
    }
}
