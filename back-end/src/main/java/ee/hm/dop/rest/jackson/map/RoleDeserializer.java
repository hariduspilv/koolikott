package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ee.hm.dop.model.Role;

/**
 * Created by mart on 30.11.16.
 */
public class RoleDeserializer extends JsonDeserializer<Role>{
    public Role deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return Role.valueOf(jp.getText());
    }
}
