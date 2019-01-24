package ee.hm.dop.rest.jackson.map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ee.hm.dop.model.Language;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Converts Language into JSON where the only field is the language code.
 */
@Component
public class LanguageSerializer extends JsonSerializer<Language> {

    @Override
    public void serialize(Language language, JsonGenerator gen, SerializerProvider arg2) throws IOException {
        gen.writeString(language.getCode());
    }
}
