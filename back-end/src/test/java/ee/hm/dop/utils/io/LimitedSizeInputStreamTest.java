package ee.hm.dop.utils.io;

import ee.hm.dop.utils.exceptions.MaxFileSizeExceededException;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class LimitedSizeInputStreamTest {

    private static final String TEST_STRING = "123";

    @Test(expected = MaxFileSizeExceededException.class)
    public void can_not_read_from_zero_length_steam() throws Exception {
        LimitedSizeInputStream limitedInputStream = new LimitedSizeInputStream(0, makeStream());
        limitedInputStream.read();
    }

    @Test
    public void can_read_from_steam() throws Exception {
        LimitedSizeInputStream limitedInputStream = new LimitedSizeInputStream(1, makeStream());
        String s = IOUtils.toString(limitedInputStream, Charsets.UTF_8);
        assertEquals(TEST_STRING, s);
        LimitedSizeInputStream limitedInputStream2 = new LimitedSizeInputStream(1, makeStream());
        assertEquals("1", getChar(limitedInputStream2));
        assertEquals("2", getChar(limitedInputStream2));
        assertEquals("3", getChar(limitedInputStream2));
        assertEquals("\uFFFF", getChar(limitedInputStream2));
        assertEquals("\uFFFF", getChar(limitedInputStream2));
    }

    private ByteArrayInputStream makeStream() throws UnsupportedEncodingException {
        return new ByteArrayInputStream(TEST_STRING.getBytes(StandardCharsets.UTF_8.name()));
    }

    private String getChar(LimitedSizeInputStream limitedInputStream) throws IOException {
        return Character.toString((char) limitedInputStream.read());
    }
}