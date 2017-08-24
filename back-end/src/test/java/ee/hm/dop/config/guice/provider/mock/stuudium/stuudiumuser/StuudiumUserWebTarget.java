package ee.hm.dop.config.guice.provider.mock.stuudium.stuudiumuser;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;

import ee.hm.dop.config.guice.provider.mock.rs.client.WebTargetMock;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;

public class StuudiumUserWebTarget extends WebTargetMock {

    MultivaluedMap<String, String> params = new MultivaluedStringMap();

    @Override
    public Builder request() {
        StuudiumUserBuilder stuudiumUserBuilder = new StuudiumUserBuilder(params);
        params = new MultivaluedStringMap();

        return stuudiumUserBuilder;
    }

    @Override
    public WebTarget queryParam(String name, Object... values) {
        for (Object value : values) {
            params.add(name, value.toString());
        }

        return this;
    }
}