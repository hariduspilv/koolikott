package ee.hm.dop.config.guice.provider.mock.harid;

import ee.hm.dop.config.guice.provider.mock.rs.client.WebTargetMock;

import javax.ws.rs.client.Invocation;

public class HaridPersonWebTarget extends WebTargetMock {

    @Override
    public Invocation.Builder request() {
        return new HaridPersonBuilder();
    }
}
