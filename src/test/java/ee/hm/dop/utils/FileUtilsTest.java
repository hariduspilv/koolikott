package ee.hm.dop.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.InputStream;

import org.easymock.EasyMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class FileUtilsTest {

    @Test
    public void getFileAsStreamNoFile() {
        InputStream inputStream = FileUtils.getFileAsStream("wrong.xml");
        assertNull(inputStream);
    }

    @Test
    public void getFileAsStream() {
        InputStream inputStream = null;
        String property = "java.io.tmpdir";
        String tempDir = System.getProperty(property);

        File dir = new File(tempDir);
        for (final File fileEntry : dir.listFiles()) {
            try {
                inputStream = FileUtils.getFileAsStream(tempDir + "/" + fileEntry.getName());
                break;
            } catch (Exception e) {
                //permission is denied for some files
            }
        }

        assertNotNull(inputStream);
    }
}
