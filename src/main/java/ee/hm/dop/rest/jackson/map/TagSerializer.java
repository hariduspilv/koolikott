package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import ee.hm.dop.model.Tag;

/**
 * Converts Tag into JSON tag string.
 * 
 * @author Jordan Silva
 *
 */
public class TagSerializer extends JsonSerializer<Tag> {

    @Override
    public void serialize(Tag tag, JsonGenerator gen, SerializerProvider arg2) throws IOException,
            JsonProcessingException {
        gen.writeString(tag.getName());
    }
}
