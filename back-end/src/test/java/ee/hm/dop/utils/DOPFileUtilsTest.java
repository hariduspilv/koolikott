package ee.hm.dop.utils;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMockRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;


@RunWith(EasyMockRunner.class)
public class DOPFileUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(DOPFileUtilsTest.class);

    @Ignore
    @Test
    public void probingDoesntFail() {
        DOPFileUtils.probeForMediaType("iii+kooliastme+?petajaraamat.pdf");
    }

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
        final String FILE_DIRECTORY = "src/test/resources/uploads/";
        final String FILE_NAME = "uploadedFileTest.test";

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(FILE_CONTENT.getBytes(StandardCharsets.UTF_8))) {
            DOPFileUtils.writeToFile(inputStream, FILE_DIRECTORY + FILE_NAME);
        } catch (IOException ignored){

        }
        String file_content = DOPFileUtils.readFileAsString(FILE_DIRECTORY + FILE_NAME);
        assertEquals(FILE_CONTENT, file_content);
        try {
            FileUtils.forceDelete(new File(FILE_DIRECTORY + FILE_NAME));
        } catch (IOException e) {
            logger.error("Could not delete file!");
        }
    }
}
