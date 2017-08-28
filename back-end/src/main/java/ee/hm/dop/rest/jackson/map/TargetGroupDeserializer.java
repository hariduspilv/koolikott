package ee.hm.dop.rest.jackson.map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.service.metadata.TargetGroupService;

import java.io.IOException;


public class TargetGroupDeserializer extends JsonDeserializer<TargetGroup> {
    @Override
    public TargetGroup deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        TargetGroupService targetGroupService = GuiceInjector.getInjector().getInstance(TargetGroupService.class);

        String targetGroupName = jp.getText();

        return targetGroupService.getByName(targetGroupName);
    }
}
