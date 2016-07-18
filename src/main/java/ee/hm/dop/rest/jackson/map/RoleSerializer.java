package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ee.hm.dop.model.Role;

/**
 * Created by mart on 18.07.16.
 */
public class RoleSerializer extends JsonSerializer<Role> {

    @Override
    public void serialize(Role role, JsonGenerator gen, SerializerProvider arg2) throws IOException {
        gen.writeString(role.toString());
    }
}
