package ee.hm.dop.config.guice.provider;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import ee.hm.dop.common.test.GuiceTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GuiceTestRunner.class)
public class SolrEngineServiceProviderTest {

    @Inject
    private SearchEngineServiceProvider searchEngineServiceProvider;

    @Test
    public void get() {
        assertNotNull(searchEngineServiceProvider.get());
    }

}
