package ee.hm.dop.rest.jackson.map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ee.hm.dop.model.Tag;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Converts Tag into JSON tag string.
 */
@Component
public class TagSerializer extends JsonSerializer<Tag> {

    @Override
    public void serialize(Tag tag, JsonGenerator gen, SerializerProvider arg2) throws IOException {
        gen.writeString(tag.getName());
    }
}

