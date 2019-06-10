package ee.hm.dop.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneId.of("UTC"));


    public static final DateTimeFormatter formatterWithoutMillis = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withZone(ZoneId.of("UTC"));


    public static final DateTimeFormatter ddMMyyyy_formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static String toString_ddMMyyyy(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(ddMMyyyy_formatter) : "";
    }

    /**
     * Converts JSON date format (yyyy-MM-dd'T'HH:mm:ss.SSS'Z' or
     * yyyy-MM-dd'T'HH:mm:ss'Z') into {@link LocalDateTime} object.
     *
     * @param jsonDate the date to be parsed
     * @return the {@link LocalDateTime} object represented by {@code jsonDate}
     */
    public static LocalDateTime fromJson(String jsonDate) {
        LocalDateTime date;

        try {
            date = LocalDateTime.parse(jsonDate, formatter);
        } catch (IllegalArgumentException ex) {
            date = LocalDateTime.parse(jsonDate, formatterWithoutMillis);
        }

        return date.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Europe/Tallinn")).toLocalDateTime();
    }

    /**
     * Converts {@link LocalDateTime} object into JSON format
     * (yyyy-MM-dd'T'HH:mm:ss.SSS'Z') String.
     *
     * @param date the date to be serialized
     * @return the JSON string representation of {@code date}
     */
    public static String toJson(LocalDateTime date) {
        return date.format(formatter);
    }

    /**
     * Converts {@link LocalDateTime} object into String using the format
     * yyyy-MM-dd'T'HH:mm:ss'Z'.
     *
     * @param date the date to be serialized
     * @return the String representation of {@code date}
     */
    public static String toStringWithoutMillis(LocalDateTime date) {
        LocalDateTime utc = date.atZone(ZoneId.of("UTC")).toLocalDateTime();
        return utc.format(formatterWithoutMillis);
    }
}
