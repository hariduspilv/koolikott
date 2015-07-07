package ee.hm.dop.rest.jackson.map;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.core.JsonParser;

/**
 * Created by mart.laus on 6.07.2015.
 */

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
        DateTime expected = new DateTime(2015, 3, 27, 23, 0, 0, 0, DateTimeZone.forID("Europe/Helsinki"));
        deserialize("2015-03-27T21:00:00.000Z", expected);
    }

    /**
     * Tests when date is created at 3am in EEST time zone (GMT+0300)
     */
    @Test
    public void deserialize3AM() {
        deserialize("2015-03-27T00:00:00.000Z",
                new DateTime(2015, 3, 27, 2, 0, 0, 0, DateTimeZone.forID("Europe/Helsinki")));
    }

    private void deserialize(String date, DateTime expected) {
        try {
            expect(jp.getText()).andReturn(date);
        } catch (IOException e) {
            //ignore
        }

        replay(jp);

        DateTime result = null;

        try {
            result = deserializer.deserialize(jp, null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        verify(jp);

        assertEquals(expected, result);
    }
}
