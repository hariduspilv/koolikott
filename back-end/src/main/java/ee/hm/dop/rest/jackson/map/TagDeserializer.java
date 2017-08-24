package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.model.Tag;
import ee.hm.dop.service.TagService;

/**
 * Converts JSON tag string into Tag.
 * 
 * @author Jordan Silva
 *
 */
public class TagDeserializer extends JsonDeserializer<Tag> {

    @Override
    public Tag deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        TagService tagService = GuiceInjector.getInjector().getInstance(TagService.class);

        String tagName = jp.getText();
        Tag tag = tagService.getTagByName(tagName);

        if (tag == null) {
            tag = new Tag();
            tag.setName(tagName);
        }

        return tag;
    }
}
