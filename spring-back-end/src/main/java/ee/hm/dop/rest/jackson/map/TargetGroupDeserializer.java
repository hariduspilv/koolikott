package ee.hm.dop.rest.jackson.map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.service.metadata.TargetGroupService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class TargetGroupDeserializer extends JsonDeserializer<TargetGroup> {

    private TargetGroupService targetGroupService;

    @Override
    public TargetGroup deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return targetGroupService.getByName(jp.getText());
    }
}
