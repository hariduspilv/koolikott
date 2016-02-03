package ee.hm.dop.guice.provider.mock.ekool.token;

import static org.junit.Assert.assertEquals;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import ee.hm.dop.guice.provider.mock.ekool.EkoolResponse;
import ee.hm.dop.guice.provider.mock.rs.client.Builder;
import ee.hm.dop.model.ekool.EkoolToken;

public class EkoolTokenBuilder extends Builder {

    private static final String AUTH_HEADER_HASH = "Basic "
            + Base64.getEncoder().encodeToString("koolikott:9rIxgey74Ke87OVYhCZfezyJ6g95UeLI9YxIhY0FuH8m".getBytes());

    private static final Map<String, EkoolToken> tokenMap;
    private static final String CODE_1 = "123456789";
    private static final String CODE_2 = "987654321";

    static {
        tokenMap = new HashMap<>();

        EkoolToken token1 = new EkoolToken();

        token1.setAccessToken("shdsajhfuh5484618");
        tokenMap.put(CODE_1, token1);

        EkoolToken token2 = new EkoolToken();
        token2.setAccessToken("54fdsgffs4566fds51dsds4g");
        tokenMap.put(CODE_2, token2);
    }

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
        assertEquals("https://localhost/rest/login/ekool/success", tokenRequestParams.get("redirect_uri").get(0));

        String code = tokenRequestParams.get("code").get(0);

        EkoolToken ekoolToken = tokenMap.get(code);
        if (ekoolToken == null) {
            throw new RuntimeException("Invalid code: " + code);
        }

        return new EkoolResponse(ekoolToken);
    }
}
