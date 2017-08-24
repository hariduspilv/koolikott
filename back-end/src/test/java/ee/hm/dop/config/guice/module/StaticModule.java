package ee.hm.dop.config.guice.module;

import com.google.inject.AbstractModule;
import ee.hm.dop.config.guice.GuiceInjector.Module;
import ee.hm.dop.server.EmbeddedJettyTest;

@Module
public class StaticModule extends AbstractModule {

    @Override
    protected void configure() {
        requestStaticInjection(EmbeddedJettyTest.class);
    }
}
