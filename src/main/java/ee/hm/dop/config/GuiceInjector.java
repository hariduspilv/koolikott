package ee.hm.dop.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.ServletModule;

import ee.hm.dop.service.HelloWorldService;

public class GuiceInjector {

    private static Injector injector;

    private static Injector createInjector() {
        return Guice.createInjector(new ServletModule() {

            @Override
            protected void configureServlets() {
                // All Services must be added here
                bind(HelloWorldService.class);
            }
        });
    }

    public static Injector getInjector() {
        if (injector == null) {
            injector = createInjector();
        }

        return injector;
    }
}