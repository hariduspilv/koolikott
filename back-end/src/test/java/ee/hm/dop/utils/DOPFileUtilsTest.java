package ee.hm.dop.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(EasyMockRunner.class)
public class DOPFileUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(DOPFileUtilsTest.class);

    @Test
    public void getFileAsStreamNoFile() {
        InputStream inputStream = DOPFileUtils.getFileAsStream("wrong.xml");
        assertNull(inputStream);
    }

    @Test
    public void getFileAsStream() throws IOException {
        // create tmp file
        File tempFile = File.createTempFile("testFileForGetFileAsStream", ".dop");
        PrintWriter writer = new PrintWriter(tempFile, "UTF-8");
        writer.println("bla bla bla");
        writer.close();
        tempFile.deleteOnExit();

        InputStream inputStream = DOPFileUtils.getFileAsStream(tempFile.getAbsolutePath());
        assertNotNull(inputStream);
    }

    @Test
    public void writeToFile() {

        final String FILE_CONTENT = "Test content";
        final String FILE_DIRECTORY = "uploads/";
        final String FILE_NAME = "uploadedFileTest.test";

        DOPFileUtils.writeToFile(new ByteArrayInputStream(FILE_CONTENT.getBytes()), FILE_DIRECTORY + FILE_NAME);
        String file_content = DOPFileUtils.readFileAsString(FILE_DIRECTORY + FILE_NAME);
        assertEquals(FILE_CONTENT, file_content);
        try {
            FileUtils.forceDelete(new File(FILE_DIRECTORY + FILE_NAME));
        } catch (IOException e) {
            logger.error("Could not delete file!");
        }
    }
}
