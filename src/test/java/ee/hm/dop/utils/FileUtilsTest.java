package ee.hm.dop.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
        InputStream inputStream = FileUtils.getFileAsStream("sarvik_metadata.xml");
        assertNotNull(inputStream);
    }
}
