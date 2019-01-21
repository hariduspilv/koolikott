package ee.hm.dop.rest.jackson.map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ee.hm.dop.model.Tag;
import ee.hm.dop.service.metadata.LanguageService;
import ee.hm.dop.service.metadata.TagService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Converts JSON tag string into Tag.
 *
 * @author Jordan Silva
 *
 */
@Component
public class TagDeserializer extends JsonDeserializer<Tag> {

    private static TagService tagService;

    /**
     * jackson uses empty constructor,
     * then spring uses one with arguments and sets
     * service static so it is shared to jackson
     */
    public TagDeserializer() { }

    @Inject
    public TagDeserializer(TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    public Tag deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String tagName = jp.getText();
        Tag tag = tagService.getTagByName(tagName);
        return tag != null ? tag : new Tag(tagName);
    }
}
