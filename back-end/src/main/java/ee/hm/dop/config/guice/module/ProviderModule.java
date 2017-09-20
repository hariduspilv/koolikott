package ee.hm.dop.config.guice.module;

import com.google.inject.AbstractModule;
import ee.hm.dop.config.db.DatabaseMigrator;
import ee.hm.dop.config.db.FlywayDbMigrator;
import ee.hm.dop.config.guice.GuiceInjector.Module;
import ee.hm.dop.config.guice.provider.*;
import ee.hm.dop.service.ehis.EhisSOAPService;
import ee.hm.dop.service.ehis.IEhisSOAPService;
import ee.hm.dop.service.solr.SolrEngineService;
import org.apache.commons.configuration.Configuration;
import org.opensaml.xml.signature.SignatureValidator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.client.Client;
import javax.xml.soap.SOAPConnection;

@Module
public class ProviderModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Configuration.class).toProvider(ConfigurationProvider.class);
        bind(EntityManagerFactory.class).toProvider(EntityManagerFactoryProvider.class);
        bind(EntityManager.class).toProvider(EntityManagerProvider.class);
        bind(Client.class).toProvider(HttpClientProvider.class);
        bind(SolrEngineService.class).toProvider(SearchEngineServiceProvider.class);
        bind(SOAPConnection.class).toProvider(SOAPConnectionProvider.class);
        bind(SignatureValidator.class).toProvider(SignatureValidatorProvider.class);
        bind(IEhisSOAPService.class).toProvider(EhisSOAPServiceProvider.class);
        bind(DatabaseMigrator.class).to(FlywayDbMigrator.class);
    }
}
