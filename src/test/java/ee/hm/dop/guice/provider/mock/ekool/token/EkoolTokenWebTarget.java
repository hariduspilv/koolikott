package ee.hm.dop.guice.provider.mock.ekool.token;

import javax.ws.rs.client.Invocation.Builder;

import ee.hm.dop.guice.provider.mock.rs.client.WebTargetMock;

public class EkoolTokenWebTarget extends WebTargetMock {

    @Override
    public Builder request() {
        return new EkoolTokenBuilder();
    }
}
