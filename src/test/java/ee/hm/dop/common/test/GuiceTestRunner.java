package ee.hm.dop.common.test;

import static ee.hm.dop.guice.GuiceInjector.getInjector;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import ee.hm.dop.guice.GuiceInjector;

/**
 * JUnit test runner that initialize Guice.
 * 
 * @author jordan
 */
public class GuiceTestRunner extends BlockJUnit4ClassRunner {

    public GuiceTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        
        GuiceInjector.init();
    }

    @Override
    public Object createTest() throws Exception {
        Object test = super.createTest();
        getInjector().injectMembers(test);
        return test;
    }
}
