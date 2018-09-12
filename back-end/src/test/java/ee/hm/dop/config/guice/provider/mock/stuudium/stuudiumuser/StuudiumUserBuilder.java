package ee.hm.dop.config.guice.provider.mock.stuudium.stuudiumuser;

import ee.hm.dop.config.guice.provider.mock.rs.client.Builder;
import ee.hm.dop.config.guice.provider.mock.stuudium.StuudiumResponse;
import ee.hm.dop.model.stuudium.StuudiumUser;
import org.apache.commons.codec.digest.HmacUtils;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class StuudiumUserBuilder extends Builder {

    private MultivaluedMap<String, String> params;

    private static final Map<String, StuudiumUser> results = map();

    private static final String STUUDIUM_CLIENT_ID = "123456789CLIENTID123456789";
    private static final String STUUDIUM_CLIENT_SECRET = "999888777TEST666555444";

    public StuudiumUserBuilder(MultivaluedMap<String, String> params) {
        this.params = params;
    }

    @Override
    public Response get() {
        String token = params.getFirst("token");
        String clientID = params.getFirst("client_id");
        String signature = params.getFirst("signature");

        validateToken(token);
        validateClientID(clientID);
        validateSignature(token, signature);

        return new StuudiumResponse(results.get(token));
    }

    @Override
    public Builder accept(String... mediaTypes) {
        return this;
    }

    private void validateClientID(String clientID) {
        if (clientID == null || !clientID.equals(STUUDIUM_CLIENT_ID)) {
            throw new RuntimeException("Invalid client id: " + clientID);
        }
    }

    private void validateToken(String token) {
        if (token == null || !(token.equals("987654") || token.equals("123223"))) {
            throw new RuntimeException("Invalid token: " + token);
        }
    }

    private void validateSignature(String token, String signature) {
        if (signature == null || !signature.equals(HmacUtils.hmacSha1Hex(STUUDIUM_CLIENT_SECRET, token))) {
            throw new RuntimeException("Invalid signature: " + signature);
        }
    }

    private static Map<String, StuudiumUser> map() {
        Map<String, StuudiumUser> results = new HashMap<>();
        results.put("987654", user("10203020100", "Juhan", "Juulius Teesaar"));
        results.put("123223", user(null, "Mida", "Iganes Iganes"));
        return results;
    }

    private static StuudiumUser user(String idCode, String mida, String iganes_iganes) {
        StuudiumUser stuudiumUser1 = new StuudiumUser();
        stuudiumUser1.setIdCode(idCode);
        stuudiumUser1.setFirstName(mida);
        stuudiumUser1.setLastName(iganes_iganes);
        return stuudiumUser1;
    }
}
