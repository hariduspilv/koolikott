package ee.hm.dop.utils;

import static org.joda.time.LocalDateTime.now;

import org.joda.time.LocalDateTime;

public class ExecutorUtil {

    /**
     * 
     * @param hourOfDayToExecute
     * @return the initial delay in milliseconds.
     */
    public static long getInitialDelay(int hourOfDayToExecute) {
        LocalDateTime todayScheduleTime = now() //
                .withHourOfDay(hourOfDayToExecute) //
                .withMinuteOfHour(0) //
                .withSecondOfMinute(0) //
                .withMillisOfSecond(0);

        LocalDateTime firstExecution = todayScheduleTime;

        if (todayScheduleTime.isBefore(now())) {
            firstExecution = todayScheduleTime.plusDays(1);
        }

        return firstExecution.toDate().getTime() - System.currentTimeMillis();
    }
}
