package ee.hm.dop.config.guice.provider.mock.ekool;

import ee.hm.dop.config.guice.provider.mock.rs.client.ResponseMock;

public class EkoolResponse extends ResponseMock {

	private Object pojo;
	
	public EkoolResponse(Object pojo) {
		this.pojo = pojo;
	}
	
	@Override
	public <T> T readEntity(Class<T> entityType) {
		return (T) pojo;
	}

}
