package ee.hm.dop.rest.jackson.map;

import java.io.IOException;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Converts LocalDate into JSON date string. As LocalDate does not have
 * information about time zone and time, time is set to midday and time zone is
 * assumed to be UTC. It may cause problems if server and client are in
 * different time zones.
 * 
 * @author Jordan Silva
 *
 */
public class LocalDateSerializer extends JsonSerializer<LocalDate> {

    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T12:00:00.000Z'");
    private static DateTimeFormatter bcFormatter = DateTimeFormat.forPattern("yyyyyy-MM-dd'T12:00:00.000Z'");

    @Override
    public void serialize(LocalDate date, JsonGenerator gen, SerializerProvider arg2) throws IOException,
            JsonProcessingException {

        if (date.getEra() == DateTimeConstants.BC) {
            gen.writeString(bcFormatter.print(date));
        } else {
            gen.writeString(formatter.print(date));
        }
    }
}
