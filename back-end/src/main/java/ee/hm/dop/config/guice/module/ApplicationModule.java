package ee.hm.dop.config.guice.module;

import com.google.inject.AbstractModule;
import ee.hm.dop.ApplicationLauncher;
import ee.hm.dop.ApplicationManager;
import ee.hm.dop.config.guice.GuiceInjector.Module;

@Module
public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        requestStaticInjection(ApplicationLauncher.class);
        requestStaticInjection(ApplicationManager.class);
    }
}
