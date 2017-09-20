package ee.hm.dop.config.guice.provider;

import com.google.inject.Provider;
import ee.hm.dop.service.ehis.EhisSOAPService;
import ee.hm.dop.service.ehis.IEhisSOAPService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EhisSOAPServiceProvider implements Provider<IEhisSOAPService> {
    @Inject
    private EhisSOAPService instance;

    @Override
    public IEhisSOAPService get() {
        return instance;
    }
}
