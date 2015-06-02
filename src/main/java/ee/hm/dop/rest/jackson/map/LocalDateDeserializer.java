package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Converts JSON date string into LocalDate.
 * 
 * @author Jordan Silva
 *
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
            JsonProcessingException {
        return new DateTime(jp.getText()).toLocalDate();
    }
}
