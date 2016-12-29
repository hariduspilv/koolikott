package ee.hm.dop.guice.provider;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

/**
 * Settings for Jackson JSON (de)serialization.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper;

    public ObjectMapperProvider() {
        mapper = new ObjectMapper();

        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);

        Hibernate4Module hibernate4Module = new Hibernate4Module();
        hibernate4Module.enable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
        hibernate4Module.disable(Hibernate4Module.Feature.USE_TRANSIENT_ANNOTATION);
        mapper.registerModule(hibernate4Module);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
