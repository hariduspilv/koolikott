package ee.hm.dop.utils;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DateUtilsTest {

    @Test
    public void fromJson() {
        String json = "2014-10-30T08:37:05Z";
        DateTime fromJson = DateUtils.fromJson(json);
        assertEquals(new DateTime(json), fromJson);

        json = "2014-10-30T08:37:05.009Z";
        fromJson = DateUtils.fromJson(json);
        assertEquals(new DateTime(json), fromJson);
    }

    @Test
    public void toJson() {
        DateTime date = new DateTime("2014-10-30T08:37:05Z");
        String json = "2014-10-30T08:37:05.000Z";
        String toJson = DateUtils.toJson(date);
        assertEquals(json, toJson);

        json = "2014-10-30T08:37:05.009Z";
        date = new DateTime(json);
        toJson = DateUtils.toJson(date);
        assertEquals(json, toJson);
    }
}
