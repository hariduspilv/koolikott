package ee.hm.dop.guice.module;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.client.Client;

import org.apache.commons.configuration.Configuration;

import com.google.inject.AbstractModule;

import ee.hm.dop.guice.GuiceInjector.Module;
import ee.hm.dop.guice.provider.ConfigurationProvider;
import ee.hm.dop.guice.provider.EntityManagerFactoryProvider;
import ee.hm.dop.guice.provider.EntityManagerProvider;
import ee.hm.dop.guice.provider.HttpClientProvider;

@Module
public class ProviderModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Configuration.class).toProvider(ConfigurationProvider.class);
        bind(EntityManagerFactory.class).toProvider(EntityManagerFactoryProvider.class);
        bind(EntityManager.class).toProvider(EntityManagerProvider.class);
        bind(Client.class).toProvider(HttpClientProvider.class);
    }
}
