package ee.hm.dop.guice.module;

import com.google.inject.AbstractModule;

import ee.hm.dop.ApplicationLauncher;
import ee.hm.dop.guice.GuiceInjector.Module;

@Module
public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        requestStaticInjection(ApplicationLauncher.class);
    }
}
