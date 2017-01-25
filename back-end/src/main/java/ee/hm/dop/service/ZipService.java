package ee.hm.dop.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipService {
    static final String ZIP_EXTENSION = ".zip";
    private List<String> fileList = new ArrayList<>();
    private String sourceFolder;
    private Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * Zip it
     *
     * @param sourceFolder source ZIP file location
     * @param outputFolder output ZIP file location
     */

    public String packArchive(String sourceFolder, String outputFolder) {
        this.sourceFolder = sourceFolder;
        String outputFile = outputFolder + ZIP_EXTENSION;
        generateFileList(new File(sourceFolder));
        zipIt(outputFile);
        return outputFile;
    }

    public void zipIt(String zipFile) {

        byte[] buffer = new byte[1024];

        try {
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            logger.info("Output to Zip : " + zipFile);

            for (String file : this.fileList) {

                logger.info("File Added : " + file);
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in =
                        new FileInputStream(sourceFolder.substring(0, sourceFolder.lastIndexOf("/")) + file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();
            zos.close();

            logger.info("Archiving completed");
        } catch (IOException ex) {
            logger.warn("Unable to zip file/directory");
        }
    }

    /**
     * Traverse a directory and get all files,
     * and add the file into fileList
     *
     * @param node file or directory
     */
    public void generateFileList(File node) {
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote) {
                generateFileList(new File(node, filename));
            }
        }

    }

    /**
     * Format the file path for zip
     *
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file) {
        return file.substring(sourceFolder.length() + 1, file.length());
    }

    public void unpackArchive(InputStream inputStream, String location) {
        byte[] buffer = new byte[1024];
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