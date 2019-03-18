package ee.hm.dop.service.synchronizer;

import ee.hm.dop.utils.DbUtils;
import ee.hm.dop.utils.ExecutorUtil;

import javax.persistence.EntityTransaction;

public abstract class DopDaemonProcess {

    public abstract void run();

    long getInitialDelay(int hourOfDayToExecute) {
        return ExecutorUtil.getInitialDelay(hourOfDayToExecute);
    }

    protected void closeTransaction() {
        DbUtils.closeTransaction();
    }

    protected void closeEntityManager() {
        DbUtils.closeEntityManager();
    }

    protected void beginTransaction() {
        EntityTransaction transaction = DbUtils.getTransaction();
        if (!transaction.isActive()) transaction.begin();
    }
}
