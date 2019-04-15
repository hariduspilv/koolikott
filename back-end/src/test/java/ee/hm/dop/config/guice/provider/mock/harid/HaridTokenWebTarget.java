package ee.hm.dop.config.guice.provider.mock.harid;

import ee.hm.dop.config.guice.provider.mock.rs.client.WebTargetMock;

import javax.ws.rs.client.Invocation;

public class HaridTokenWebTarget extends WebTargetMock {

    @Override
    public Invocation.Builder request() {
        return new HaridTokenBuilder();
    }
}
