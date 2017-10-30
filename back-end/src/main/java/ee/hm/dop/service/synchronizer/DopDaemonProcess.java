package ee.hm.dop.service.synchronizer;

import ee.hm.dop.utils.DbUtils;
import ee.hm.dop.utils.ExecutorUtil;

public abstract class DopDaemonProcess {

    public abstract void run();

    public abstract void stop();

    long getInitialDelay(int hourOfDayToExecute) {
        return ExecutorUtil.getInitialDelay(hourOfDayToExecute);
    }

    protected void closeTransaction() {
        DbUtils.closeTransaction();
    }

    protected void beginTransaction() {
        DbUtils.getTransaction().begin();
    }

}
