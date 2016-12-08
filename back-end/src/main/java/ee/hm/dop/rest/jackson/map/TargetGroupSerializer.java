package ee.hm.dop.rest.jackson.map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ee.hm.dop.model.TargetGroup;

import java.io.IOException;

public class TargetGroupSerializer extends JsonSerializer<TargetGroup> {

    @Override
    public void serialize(TargetGroup targetGroup, JsonGenerator gen, SerializerProvider arg2) throws IOException,
            JsonProcessingException {
        gen.writeString(targetGroup.getName());
    }
}
