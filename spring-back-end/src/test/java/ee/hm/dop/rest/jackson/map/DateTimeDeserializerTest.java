package ee.hm.dop.rest.jackson.map;

import com.fasterxml.jackson.core.JsonParser;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

@RunWith(EasyMockRunner.class)
public class DateTimeDeserializerTest {

    @TestSubject
    private DateTimeDeserializer deserializer = new DateTimeDeserializer();
    @Mock
    private JsonParser jp;

    /**
     * Tests when date is created at midnight in EEST time zone (GMT+0300)
     */
    @Test
    public void deserializeMidnight() {
        String testDate = "2015-03-27T21:00:00.000Z";
        //todo had timezone
        LocalDateTime expected = LocalDateTime.parse(testDate);
        deserialize(testDate, expected);
    }

    /**
     * Tests when date is created at 3am in EEST time zone (GMT+0300)
     */
    @Test
    public void deserialize3AM() {
        String testDate = "2015-03-27T00:00:00.000Z";
        //todo had timezone
        LocalDateTime expected = LocalDateTime.parse(testDate);
        deserialize(testDate, expected);
    }

    private void deserialize(String date, LocalDateTime expected) {
        try {
            expect(jp.getText()).andReturn(date);
        } catch (IOException ignored) {
        }

        replay(jp);

        LocalDateTime result;

        try {
            result = deserializer.deserialize(jp, null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        verify(jp);

        assertEquals(expected, result);
    }
}
