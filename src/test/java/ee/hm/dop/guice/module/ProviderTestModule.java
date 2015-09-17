package ee.hm.dop.guice.module;

import javax.persistence.EntityManagerFactory;
import javax.xml.soap.SOAPConnection;

import org.apache.commons.configuration.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;

import ee.hm.dop.guice.GuiceInjector.Module;
import ee.hm.dop.guice.provider.ConfigurationTestProvider;
import ee.hm.dop.guice.provider.EntityManagerFactoryTestProvider;
import ee.hm.dop.guice.provider.ObjectMapperGuiceProvider;
import ee.hm.dop.guice.provider.SOAPConnectionTestProvider;
import ee.hm.dop.guice.provider.SearchEngineServiceTestProvider;
import ee.hm.dop.service.SearchEngineService;

@Module(override = ProviderModule.class)
public class ProviderTestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EntityManagerFactory.class).toProvider(EntityManagerFactoryTestProvider.class);
        bind(Configuration.class).toProvider(ConfigurationTestProvider.class);
        bind(SearchEngineService.class).toProvider(SearchEngineServiceTestProvider.class);
        bind(ObjectMapper.class).toProvider(ObjectMapperGuiceProvider.class);
        bind(SOAPConnection.class).toProvider(SOAPConnectionTestProvider.class);
    }
}
