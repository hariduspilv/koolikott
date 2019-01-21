package ee.hm.dop.rest.jackson.map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.service.metadata.TargetGroupService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;

@Component
public class TargetGroupDeserializer extends JsonDeserializer<TargetGroup> {

    @Inject
    private TargetGroupService targetGroupService;

    @Override
    public TargetGroup deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return targetGroupService.getByName(jp.getText());
    }
}
