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

    private static DateTimeFormatter ddMMyyyy_formatter = DateTimeFormat.forPattern("dd.MM.yyyy").withZoneUTC();

    public static String toString_ddMMyyyy(DateTime dateTime){
        return dateTime != null ? dateTime.toString(ddMMyyyy_formatter) : "";
    }

    /**
     * Converts JSON date format (yyyy-MM-dd'T'HH:mm:ss.SSS'Z' or
     * yyyy-MM-dd'T'HH:mm:ss'Z') into {@link DateTime} object.
     *
     * @param jsonDate
     *            the date to be parsed
     * @return the {@link DateTime} object represented by {@code jsonDate}
     */
    public static DateTime fromJson(String jsonDate) {
        DateTime date;

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

    /**
     * Converts {@link DateTime} object into String using the format
     * yyyy-MM-dd'T'HH:mm:ss'Z'.
     *
     * @param date
     *            the date to be serialized
     * @return the String representation of {@code date}
     */
    public static String toStringWithoutMillis(DateTime date) {
        return formatterWithoutMillis.print(date.withZone(DateTimeZone.UTC));
    }
}
