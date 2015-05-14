package ee.hm.dop.guice;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.configuration.Configuration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;

import ee.hm.dop.guice.provider.ConfigurationProvider;
import ee.hm.dop.guice.provider.EntityManagerFactoryProvider;
import ee.hm.dop.guice.provider.EntityManagerProvider;
import ee.hm.dop.service.MaterialService;

public class GuiceInjector {

    private static Injector injector;

    private static Injector createInjector() {
        return Guice.createInjector(new ServletModule() {

            @Override
            protected void configureServlets() {
            	bindProviders();
            	bindServices();
            }

			private void bindServices() {
				bind(MaterialService.class);
			}

			private void bindProviders() {
				bind(Configuration.class).toProvider(ConfigurationProvider.class).in(Scopes.SINGLETON);
            	bind(EntityManagerFactory.class).toProvider(EntityManagerFactoryProvider.class).in(Scopes.SINGLETON);
            	bind(EntityManager.class).toProvider(EntityManagerProvider.class);
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