package ee.hm.dop.config.guice.provider.mock.harid;

import ee.hm.dop.config.guice.provider.mock.rs.client.ResponseMock;

public class HaridResponse extends ResponseMock {
    private Object pojo;

    public HaridResponse(Object pojo) {
        this.pojo = pojo;
    }

    @Override
    public <T> T readEntity(Class<T> entityType) {
        return (T) pojo;
    }
}
