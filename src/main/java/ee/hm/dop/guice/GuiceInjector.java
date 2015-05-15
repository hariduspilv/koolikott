package ee.hm.dop.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import ee.hm.dop.guice.module.ProviderModule;
import ee.hm.dop.guice.module.RestModule;

public class GuiceInjector {

    private static Injector injector;

    public static void init(Module... modules) {
        if (injector == null) {
            injector = Guice.createInjector(modules);
        }
    }

    public static void initDefaultConfiguration() {
        init(new RestModule(), new ProviderModule());
    }

    public static Injector getInjector() {
        if (injector == null) {
            initDefaultConfiguration();
        }

        return injector;
    }
}