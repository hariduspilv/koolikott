package ee.hm.dop.config.guice.provider.mock.harid;

import ee.hm.dop.config.guice.provider.mock.rs.client.Builder;
import ee.hm.dop.model.harid.HarIdCode;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class HaridTokenBuilder extends Builder {

    private static final String AUTH_HEADER_HASH = "Basic "
            + Base64.getEncoder().encodeToString("123456789CLIENTID123456789:999888777TEST666555444".getBytes(StandardCharsets.UTF_8));

    private static final Map<String, HarIdCode> tokenMap = initMap();
    private static final String CODE_1 = "123456789";
    private static final String CODE_2 = "987654321";
    private static final String CODE_3 = "123123456";

    @Override
    public Builder header(String name, Object value) {
        assertEquals("Authorization", name);
        assertEquals(AUTH_HEADER_HASH, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Response post(Entity<?> entity) {
        MultivaluedMap<String, String> tokenRequestParams = (MultivaluedMap<String, String>) entity.getEntity();

        assertEquals("authorization_code", tokenRequestParams.get("grant_type").get(0));
        assertEquals("https://localhost/rest/login/harid/success", tokenRequestParams.get("redirect_uri").get(0));

        String code = tokenRequestParams.get("code").get(0);

        HarIdCode haridcode= tokenMap.get(code);
        if (haridcode == null) {
            throw new RuntimeException("Invalid code: " + code);
        }

        return new HaridResponse(haridcode);
    }

    private static HashMap<String, HarIdCode> initMap() {
        HashMap<String, HarIdCode> tokenMap = new HashMap<>();
        tokenMap.put(CODE_1, getHaridCode("shdsajhfuh5484618"));
        tokenMap.put(CODE_2, getHaridCode("54fdsgffs4566fds51dsds4g"));
        tokenMap.put(CODE_3, getHaridCode("3453455sdfjbljkabsfb"));
        return tokenMap;
    }

    private static HarIdCode getHaridCode(String accessToken) {
        HarIdCode token1 = new HarIdCode();
        token1.setAccessToken(accessToken);
        return token1;
    }
}
