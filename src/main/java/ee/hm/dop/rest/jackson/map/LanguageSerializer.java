package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import ee.hm.dop.model.Language;

/**
 * Converts LocalDate into JSON date string. As LocalDate does not have
 * information about time zone and time, time is set to midday and time zone is
 * assumed to be UTC. It may cause problems if server and client are in
 * different time zones.
 * 
 * @author Jordan Silva
 *
 */
public class LanguageSerializer extends JsonSerializer<Language> {

    @Override
    public void serialize(Language language, JsonGenerator gen, SerializerProvider arg2) throws IOException,
            JsonProcessingException {
        gen.writeString(language.getCode());
    }
}
