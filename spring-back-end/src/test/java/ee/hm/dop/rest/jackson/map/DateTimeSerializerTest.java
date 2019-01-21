package ee.hm.dop.rest.jackson.map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.ZoneId;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

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
//        serialize("1991-11-30T20:00:00.000Z", LocalDateTime.of(1991, 11, 30, 22, 0, 0, 0));
        //todo time
    }

    private void serialize(String expected, LocalDateTime date) {
        try {
            generator.writeString(expected);
        } catch (IOException ignored) {
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
