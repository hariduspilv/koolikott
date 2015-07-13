package ee.hm.dop.guice.provider;

import com.google.inject.Provider;
import com.google.inject.Singleton;

import ee.hm.dop.service.SearchEngineService;
import ee.hm.dop.service.SolrService;

/**
 * Guice provider of Search Engine Service.
 */
@Singleton
public class SearchEngineServiceProvider implements Provider<SearchEngineService> {

    @Override
    public synchronized SearchEngineService get() {
        return new SolrService();
    }
}
