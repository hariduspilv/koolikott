package ee.hm.dop.utils.io;

import ee.hm.dop.utils.exceptions.MaxFileSizeExceededException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class LimitedSizeInputStreamTest {

    private static final String TEST_STRING = "123";

    @Test(expected = MaxFileSizeExceededException.class)
    public void can_not_read_from_zero_length_steam() throws Exception {
        InputStream stream = new ByteArrayInputStream(TEST_STRING.getBytes(StandardCharsets.UTF_8.name()));
        LimitedSizeInputStream limitedInputStream = new LimitedSizeInputStream(0, stream);
        limitedInputStream.read();
    }

    @Test
    public void can_read_from_steam() throws Exception {
        InputStream stream = new ByteArrayInputStream(TEST_STRING.getBytes(StandardCharsets.UTF_8.name()));
        LimitedSizeInputStream limitedInputStream = new LimitedSizeInputStream(1, stream);
        assertEquals("1", getChar(limitedInputStream));
        assertEquals("2", getChar(limitedInputStream));
        assertEquals("3", getChar(limitedInputStream));
        assertEquals("\uFFFF", getChar(limitedInputStream));
        assertEquals("\uFFFF", getChar(limitedInputStream));
    }

    private String getChar(LimitedSizeInputStream limitedInputStream) throws IOException {
        return Character.toString((char) limitedInputStream.read());
    }
}