package ee.hm.dop.service.login;

import static ee.hm.dop.utils.ConfigurationProperties.STUUDIUM_CLIENT_ID;
import static ee.hm.dop.utils.ConfigurationProperties.STUUDIUM_CLIENT_SECRET;
import static ee.hm.dop.utils.ConfigurationProperties.STUUDIUM_URL_AUTHORIZE;
import static ee.hm.dop.utils.ConfigurationProperties.STUUDIUM_URL_GENERALDATA;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.stuudium.StuudiumUser;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.configuration.Configuration;

public class StuudiumService {

    @Inject
    private Configuration configuration;
    @Inject
    private Client client;
    @Inject
    private LoginService loginService;

    public AuthenticatedUser authenticate(String token) {
        StuudiumUser stuudiumUser = getStuudiumUser(token);
        return loginService.login(stuudiumUser.getIdCode(), stuudiumUser.getFirstName(), stuudiumUser.getLastName());
    }

    private StuudiumUser getStuudiumUser(String token) {
        Response response = client.target(getUserDataUrl())
                .queryParam("token", token)
                .queryParam("client_id", getClientId())
                .queryParam("signature", HmacUtils.hmacSha1Hex(getClientSecret(), token))
                .request().accept(MediaType.APPLICATION_JSON).get();

        return response.readEntity(StuudiumUser.class);
    }

    public String getAuthorizationUrl() {
        return configuration.getString(STUUDIUM_URL_AUTHORIZE);
    }

    public String getClientId() {
        return configuration.getString(STUUDIUM_CLIENT_ID);
    }

    private String getClientSecret() {
        return configuration.getString(STUUDIUM_CLIENT_SECRET);
    }

    private String getUserDataUrl() {
        return configuration.getString(STUUDIUM_URL_GENERALDATA);
    }

}
