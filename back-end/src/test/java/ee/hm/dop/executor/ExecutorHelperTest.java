package ee.hm.dop.executor;

import static org.joda.time.LocalDateTime.now;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDateTime;
import org.junit.Test;

public class ExecutorHelperTest {

    @Test
    public void getInitialDelayExecuteTomorrow() {
        LocalDateTime now = now();
        int hourOfDayToExecute = now.getHourOfDay() - 1;
        if(hourOfDayToExecute == -1){hourOfDayToExecute = 23;}
        LocalDateTime expectedExecutionTime = now.withHourOfDay(hourOfDayToExecute).withMinuteOfHour(0)
                .withSecondOfMinute(0).withMillisOfSecond(0).plusDays(1);

        int delay = (int) ExecutorHelper.getInitialDelay(hourOfDayToExecute);
        LocalDateTime firstExecution = now.plusMillis(delay);

        assertTrue(Math.abs(firstExecution.toDate().getTime() - expectedExecutionTime.toDate().getTime()) < 100);
    }

    @Test
    public void getInitialDelayExecuteToday() {
        LocalDateTime now = now();
        int hourOfDayToExecute = (now.getHourOfDay() + 1) % 23;
        LocalDateTime expectedExecutionTime = now.withHourOfDay(hourOfDayToExecute).withMinuteOfHour(0)
                .withSecondOfMinute(0).withMillisOfSecond(0);

        int delay = (int) ExecutorHelper.getInitialDelay(hourOfDayToExecute);
        LocalDateTime firstExecution = now.plusMillis(delay);

        assertTrue(Math.abs(firstExecution.toDate().getTime() - expectedExecutionTime.toDate().getTime()) < 100);
    }
}
