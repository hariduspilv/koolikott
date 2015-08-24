package ee.hm.dop.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtils {

    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZoneUTC();

    private static DateTimeFormatter formatterWithoutMillis = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withZoneUTC();

    /**
     * Converts JSON date format (yyyy-MM-dd'T'HH:mm:ss.SSS'Z' or
     * yyyy-MM-dd'T'HH:mm:ss'Z') into {@link DateTime} object.
     *
     * @param jsonDate
     *            the date to be parsed
     * @return the {@link DateTime} object represented by {@code jsonDate}
     */
    public static DateTime fromJson(String jsonDate) {
        DateTime date = null;

        try {
            date = formatter.parseDateTime(jsonDate);
        } catch (IllegalArgumentException ex) {
            date = formatterWithoutMillis.parseDateTime(jsonDate);
        }

        return date.withZone(DateTimeZone.getDefault());
    }

    /**
     * Converts {@link DateTime} object into JSON format
     * (yyyy-MM-dd'T'HH:mm:ss.SSS'Z') String.
     *
     * @param date
     *            the date to be serialized
     * @return the JSON string representation of {@code date}
     */
    public static String toJson(DateTime date) {
        return formatter.print(date.withZone(DateTimeZone.UTC));
    }
}
