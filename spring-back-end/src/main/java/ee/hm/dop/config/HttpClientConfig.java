package ee.hm.dop.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ee.hm.dop.rest.filter.DopClientRequestFilter;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Configuration
public class HttpClientConfig {

    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);

        return mapper;
    }

    @Bean
    public HTTPRedirectDeflateEncoder httpRedirectDeflateEncoder() {
        return new HTTPRedirectDeflateEncoder();
    }

    @Bean
    public Client httpClient() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.READ_TIMEOUT, 60000); // ms
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 60000); // ms

        Client client = ClientBuilder.newClient(clientConfig);
        client.register(JacksonFeature.class);
        client.register(objectMapper());
        client.register(new DopClientRequestFilter());

        return client;
    }
}
