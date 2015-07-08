package ee.hm.dop.rest.jackson.map;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Created by mart.laus on 6.07.2015.
 */

@RunWith(EasyMockRunner.class)
public class DateTimeSerializerTest {

    @TestSubject
    private DateTimeSerializer serializer = new DateTimeSerializer();

    @Mock
    private JsonGenerator generator;

    @Mock
    private SerializerProvider serializerProvider;

    @Test
    public void serializeDatetime() {
        serialize("2015-06-02T20:00:00.000Z",
                new DateTime(2015, 6, 2, 23, 0, 0, 0, DateTimeZone.forID("Europe/Helsinki")));
    }

    private void serialize(String expected, DateTime date) {
        try {
            generator.writeString(expected);
        } catch (IOException e) {
            //ignore
        }
        replay(generator, serializerProvider);

        try {
            serializer.serialize(date, generator, serializerProvider);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        verify(generator, serializerProvider);
    }
}
