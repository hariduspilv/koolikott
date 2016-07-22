package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ee.hm.dop.utils.DateUtils;
import org.joda.time.DateTime;

/**
 * Created by mart.laus on 6.07.2015.
 */
public class DateTimeDeserializer extends JsonDeserializer<DateTime> {

    @Override
    public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        return DateUtils.fromJson(jp.getText());
    }
}
