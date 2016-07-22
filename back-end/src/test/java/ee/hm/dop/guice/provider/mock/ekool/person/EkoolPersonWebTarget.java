package ee.hm.dop.guice.provider.mock.ekool.person;

import javax.ws.rs.client.Invocation.Builder;

import ee.hm.dop.guice.provider.mock.rs.client.WebTargetMock;

public class EkoolPersonWebTarget extends WebTargetMock {

    @Override
    public Builder request() {
        return new EkoolPersonBuilder();
    }
}
