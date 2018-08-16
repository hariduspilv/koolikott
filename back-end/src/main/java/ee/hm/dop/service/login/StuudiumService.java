package ee.hm.dop.service.login;

import ee.hm.dop.model.stuudium.StuudiumUser;
import ee.hm.dop.service.login.dto.UserStatus;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static ee.hm.dop.utils.ConfigurationProperties.STUUDIUM_CLIENT_ID;
import static ee.hm.dop.utils.ConfigurationProperties.STUUDIUM_CLIENT_SECRET;
import static ee.hm.dop.utils.ConfigurationProperties.STUUDIUM_URL_AUTHORIZE;
import static ee.hm.dop.utils.ConfigurationProperties.STUUDIUM_URL_GENERALDATA;

public class StuudiumService {

    @Inject
    private Configuration configuration;
    @Inject
    private Client client;
    @Inject
    private LoginService loginService;
    private HmacUtils hmacUtils;

    @Inject
    public void postConstruct() {
        postConstruct(configuration.getString(STUUDIUM_CLIENT_SECRET));
    }

    void postConstruct(String secret) {
        hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secret);
    }

    public UserStatus authenticate(String token) {
        StuudiumUser stuudiumUser = getStuudiumUser(token);
        return loginService.login(stuudiumUser.getIdCode(), stuudiumUser.getFirstName(), stuudiumUser.getLastName());
    }

    private StuudiumUser getStuudiumUser(String token) {
        Response response = client.target(getUserDataUrl())
                .queryParam("token", token)
                .queryParam("client_id", getClientId())
                .queryParam("signature", hmacUtils.hmacHex(token))
                .request().accept(MediaType.APPLICATION_JSON).get();

        return response.readEntity(StuudiumUser.class);
    }

    public String getAuthorizationUrl() {
        return configuration.getString(STUUDIUM_URL_AUTHORIZE);
    }

    public String getClientId() {
        return configuration.getString(STUUDIUM_CLIENT_ID);
    }

    private String getUserDataUrl() {
        return configuration.getString(STUUDIUM_URL_GENERALDATA);
    }

}
