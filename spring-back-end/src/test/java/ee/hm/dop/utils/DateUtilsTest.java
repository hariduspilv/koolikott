package ee.hm.dop.utils;

import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class DateUtilsTest {

    @Test
    public void x() {
        String s = DateUtils.toString_ddMMyyyy(LocalDateTime.of(2000, 1, 2, 3, 4));
        assertEquals("02.01.2000", s);
    }

    @Test
    public void fromJson() {
        String json = "2014-10-30T08:37:05Z";
        LocalDateTime fromJson = DateUtils.fromJson(json);
        assertEquals(LocalDateTime.parse(json), fromJson);

        json = "2014-10-30T08:37:05.009Z";
        fromJson = DateUtils.fromJson(json);
        assertEquals(LocalDateTime.parse(json), fromJson);
    }

    @Test
    public void toJson() {
        LocalDateTime date = LocalDateTime.parse("2014-10-30T08:37:05Z");
        String json = "2014-10-30T08:37:05.000Z";
        String toJson = DateUtils.toJson(date);
        assertEquals(json, toJson);

        json = "2014-10-30T08:37:05.009Z";
        date = LocalDateTime.parse(json);
        toJson = DateUtils.toJson(date);
        assertEquals(json, toJson);
    }

    @Test
    public void toStringWithoutMillis() {
        LocalDateTime date = LocalDateTime.parse("2014-10-30T08:37:05Z");
        String expected = "2014-10-30T08:37:05Z";
        String result = DateUtils.toStringWithoutMillis(date);
        assertEquals(expected, result);

        date = LocalDateTime.parse("2014-10-30T08:37:05.452Z");
        result = DateUtils.toStringWithoutMillis(date);
        assertEquals(expected, result);
    }
}
