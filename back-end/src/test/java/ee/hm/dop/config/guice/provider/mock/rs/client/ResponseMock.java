package ee.hm.dop.config.guice.provider.mock.rs.client;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

public class ResponseMock extends Response {

	@Override
	public int getStatus() {
		return 0;
	}

	@Override
	public StatusType getStatusInfo() {
		return null;
	}

	@Override
	public Object getEntity() {
		return null;
	}

	@Override
	public <T> T readEntity(Class<T> entityType) {
		return null;
	}

	@Override
	public <T> T readEntity(GenericType<T> entityType) {
		return null;
	}

	@Override
	public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
		return null;
	}

	@Override
	public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
		return null;
	}

	@Override
	public boolean hasEntity() { 
		return false;
	}

	@Override
	public boolean bufferEntity() {
		return false;
	}

	@Override
	public void close() {

	}

	@Override
	public MediaType getMediaType() {
		return null;
	}

	@Override
	public Locale getLanguage() {
		return null;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public Set<String> getAllowedMethods() {
		return null;
	}

	@Override
	public Map<String, NewCookie> getCookies() { 
		return null;
	}

	@Override
	public EntityTag getEntityTag() { 
		return null;
	}

	@Override
	public Date getDate() {
		return null;
	}

	@Override
	public Date getLastModified() {
		return null;
	}

	@Override
	public URI getLocation() {
		return null;
	}

	@Override
	public Set<Link> getLinks() { 
		return null;
	}

	@Override
	public boolean hasLink(String relation) {
		return false;
	}

	@Override
	public Link getLink(String relation) {
		return null;
	}

	@Override
	public Builder getLinkBuilder(String relation) {
		return null;
	}

	@Override
	public MultivaluedMap<String, Object> getMetadata() { 
		return null;
	}

	@Override
	public MultivaluedMap<String, String> getStringHeaders() {	 
		return null;
	}

	@Override
	public String getHeaderString(String name) {
		return null;
	}

}
