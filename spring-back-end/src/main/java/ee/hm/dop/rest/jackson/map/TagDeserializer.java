package ee.hm.dop.rest.jackson.map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ee.hm.dop.model.Tag;
import ee.hm.dop.service.metadata.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Converts JSON tag string into Tag.
 *
 * @author Jordan Silva
 *
 */
@Component
@AllArgsConstructor
public class TagDeserializer extends JsonDeserializer<Tag> {

    private TagService tagService;

    @Override
    public Tag deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String tagName = jp.getText();
        Tag tag = tagService.getTagByName(tagName);

        if (tag == null) {
            tag = new Tag();
            tag.setName(tagName);
        }

        return tag;
    }
}
