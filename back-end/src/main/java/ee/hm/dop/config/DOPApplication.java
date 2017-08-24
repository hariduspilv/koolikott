package ee.hm.dop.config;

import javax.inject.Inject;

import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.config.guice.provider.ObjectMapperProvider;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

public class DOPApplication extends ResourceConfig {

    @Inject
    public DOPApplication(ServiceLocator serviceLocator) {
        // Set package to look for resources in
        packages("ee.hm.dop");

        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);

        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(GuiceInjector.getInjector());

        register(JacksonFeature.class);
        register(ObjectMapperProvider.class);
        register(RolesAllowedDynamicFeature.class);
        register(MultiPartFeature.class);
    }
}