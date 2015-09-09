package ee.hm.dop.guice.provider;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.common.test.GuiceTestRunner;

@RunWith(GuiceTestRunner.class)
public class SearchEngineServiceProviderTest {

    @Inject
    private SearchEngineServiceProvider searchEngineServiceProvider;

    @Test
    public void get() {
        assertNotNull(searchEngineServiceProvider.get());
    }

}
