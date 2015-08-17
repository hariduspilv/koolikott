package ee.hm.dop.guice.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Provider for Object Mapper to be used in tests. This is needed because
 * ObjectMapperProvider is not a Guice provider. Therefore Guice is
 * Instantiating a non configured object for tests.
 */
public class ObjectMapperGuiceProvider implements Provider<ObjectMapper> {

    @Inject
    private ObjectMapperProvider objectMapperProvider;

    @Override
    public ObjectMapper get() {
        return objectMapperProvider.getContext(null);
    }
}
