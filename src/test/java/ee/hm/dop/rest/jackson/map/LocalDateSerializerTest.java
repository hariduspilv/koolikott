package ee.hm.dop.rest.jackson.map;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

@RunWith(EasyMockRunner.class)
public class LocalDateSerializerTest {

    @TestSubject
    private LocalDateSerializer serializer = new LocalDateSerializer();

    @Mock
    private JsonGenerator generator;

    @Mock
    private SerializerProvider serializerProvider;

    @Test
    public void serializeADDates() {
        serialize("2015-06-02T12:00:00.000Z", new LocalDate(2015, 6, 2));
    }

    @Test
    public void serializeBCDates() {
        serialize("-002015-06-02T12:00:00.000Z", new LocalDate(-2015, 6, 2));
    }

    private void serialize(String expected, LocalDate date) {
        try {
            generator.writeString(expected);
        } catch (IOException e) {
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
