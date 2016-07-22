package ee.hm.dop.common.test;

import ee.hm.dop.ApplicationLauncher;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * Base class for all integration tests.
 */
@RunWith(GuiceTestRunner.class)
public abstract class IntegrationTestBase {

    private static final int STARTUP_DELAY = 3000;

    private static boolean isApplicationStarted;

    private static void startApplication() throws Exception {
        ApplicationLauncher.startApplication();
        Thread.sleep(STARTUP_DELAY);
        isApplicationStarted = true;
    }

    private static void stopApplication() throws Exception {
        ApplicationLauncher.stopApplication();
        isApplicationStarted = false;
    }

    @BeforeClass
    synchronized public static void start() throws Exception {
        if (!isApplicationStarted) {
            startApplication();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        stopApplication();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
