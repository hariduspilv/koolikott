package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import ee.hm.dop.utils.DateUtils;

/**
 * Created by mart.laus on 6.07.2015.
 */
public class DateTimeSerializer extends JsonSerializer<DateTime> {

    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public void serialize(DateTime date, JsonGenerator gen, SerializerProvider provider) throws IOException {

        gen.writeString(DateUtils.toJson(date));
    }
}
