package ee.hm.dop.guice.module;

import com.google.inject.AbstractModule;
import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.guice.GuiceInjector.Module;

@Module()
public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        requestStaticInjection(ResourceIntegrationTestBase.class);
    }
}
