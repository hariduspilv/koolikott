package ee.hm.dop.guice.provider;

import javax.inject.Inject;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import ee.hm.dop.service.SolrEngineService;
import ee.hm.dop.service.SolrService;

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
