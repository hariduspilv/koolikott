package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ee.hm.dop.utils.DateUtils;
import org.joda.time.DateTime;

public class DateTimeSerializer extends JsonSerializer<DateTime> {

    @Override
    public void serialize(DateTime date, JsonGenerator gen, SerializerProvider provider) throws IOException {

        gen.writeString(DateUtils.toJson(date));
    }
}
