package ee.hm.dop.config.guice.provider.mock.stuudium;

import ee.hm.dop.config.guice.provider.mock.rs.client.ResponseMock;

public class StuudiumResponse extends ResponseMock {

    private Object pojo;

    public StuudiumResponse(Object pojo) {
        this.pojo = pojo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readEntity(Class<T> entityType) {
        return (T) pojo;
    }

}
