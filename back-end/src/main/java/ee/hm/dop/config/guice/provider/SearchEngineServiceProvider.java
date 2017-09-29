package ee.hm.dop.config.guice.provider;

import javax.inject.Inject;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import ee.hm.dop.service.solr.SolrEngineService;
import ee.hm.dop.service.solr.SolrService;

/**
 * Guice provider of Search Engine Service.
 */
@Singleton
public class SearchEngineServiceProvider implements Provider<SolrEngineService> {

    @Inject
    private SolrService instance;

    @Override
    public synchronized SolrEngineService get() {
        return instance;
    }
}
