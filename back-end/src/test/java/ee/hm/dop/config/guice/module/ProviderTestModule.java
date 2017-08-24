package ee.hm.dop.config.guice.module;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.client.Client;
import javax.xml.soap.SOAPConnection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import ee.hm.dop.config.db.DatabaseMigrator;
import ee.hm.dop.config.db.InactiveDbMigrator;
import ee.hm.dop.config.guice.GuiceInjector.Module;
import ee.hm.dop.config.guice.provider.ConfigurationTestProvider;
import ee.hm.dop.config.guice.provider.EntityManagerFactoryTestProvider;
import ee.hm.dop.config.guice.provider.HttpClientTestProvider;
import ee.hm.dop.config.guice.provider.ObjectMapperGuiceProvider;
import ee.hm.dop.config.guice.provider.SOAPConnectionTestProvider;
import ee.hm.dop.config.guice.provider.SearchEngineServiceTestProvider;
import ee.hm.dop.config.guice.provider.SignatureValidatorTestProvider;
import ee.hm.dop.service.solr.SolrEngineService;
import org.apache.commons.configuration.Configuration;
import org.opensaml.xml.signature.SignatureValidator;

@Module(override = ProviderModule.class)
public class ProviderTestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EntityManagerFactory.class).toProvider(EntityManagerFactoryTestProvider.class);
        bind(Configuration.class).toProvider(ConfigurationTestProvider.class);
        bind(SolrEngineService.class).toProvider(SearchEngineServiceTestProvider.class);
        bind(ObjectMapper.class).toProvider(ObjectMapperGuiceProvider.class);
        bind(SOAPConnection.class).toProvider(SOAPConnectionTestProvider.class);
        bind(Client.class).toProvider(HttpClientTestProvider.class);
        bind(SignatureValidator.class).toProvider(SignatureValidatorTestProvider.class);
        bind(DatabaseMigrator.class).to(InactiveDbMigrator.class);
    }
}
