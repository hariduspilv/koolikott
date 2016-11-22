package ee.hm.dop.rest.jackson.map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ee.hm.dop.model.Size;

import java.io.IOException;

public class SizeSerializer extends JsonSerializer<Size> {

    @Override
    public void serialize(Size size, JsonGenerator gen, SerializerProvider arg2) throws IOException {
        gen.writeString(size.toString());
    }
}
