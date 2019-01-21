package ee.hm.dop.rest.jackson.map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ee.hm.dop.utils.DateUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import java.io.IOException;

@Component
public class DateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime date, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(DateUtils.toJson(date));
    }
}
