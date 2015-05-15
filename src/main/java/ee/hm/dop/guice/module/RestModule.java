package ee.hm.dop.guice.module;

import com.google.inject.servlet.ServletModule;

import ee.hm.dop.service.MaterialService;

public class RestModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(MaterialService.class);
    }
}
