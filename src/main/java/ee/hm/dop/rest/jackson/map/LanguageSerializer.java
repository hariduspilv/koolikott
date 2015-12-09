package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import ee.hm.dop.model.Language;

/**
 * Converts Language into JSON where the only field is the language code.
 *
 * @author Jordan Silva
 */
public class LanguageSerializer extends JsonSerializer<Language> {

    @Override
    public void serialize(Language language, JsonGenerator gen, SerializerProvider arg2) throws IOException {
        gen.writeString(language.getCode());
    }
}
