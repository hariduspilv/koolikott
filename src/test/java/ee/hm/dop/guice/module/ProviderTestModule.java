package ee.hm.dop.guice.module;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.client.Client;
import javax.xml.soap.SOAPConnection;

import org.apache.commons.configuration.Configuration;
import org.opensaml.xml.signature.SignatureValidator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;

import ee.hm.dop.db.DatabaseMigrator;
import ee.hm.dop.db.InactiveDbMigrator;
import ee.hm.dop.guice.GuiceInjector.Module;
import ee.hm.dop.guice.provider.ConfigurationTestProvider;
import ee.hm.dop.guice.provider.EntityManagerFactoryTestProvider;
import ee.hm.dop.guice.provider.HttpClientTestProvider;
import ee.hm.dop.guice.provider.ObjectMapperGuiceProvider;
import ee.hm.dop.guice.provider.SOAPConnectionTestProvider;
import ee.hm.dop.guice.provider.SearchEngineServiceTestProvider;
import ee.hm.dop.guice.provider.SignatureValidatorTestProvider;
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
        bind(Client.class).toProvider(HttpClientTestProvider.class);
        bind(SignatureValidator.class).toProvider(SignatureValidatorTestProvider.class);
        bind(DatabaseMigrator.class).to(InactiveDbMigrator.class);
    }
}
