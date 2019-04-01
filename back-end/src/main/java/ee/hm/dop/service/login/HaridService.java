package ee.hm.dop.service.login;

import ee.hm.dop.model.enums.LoginFrom;
import ee.hm.dop.model.harid.HarIdUser;
import ee.hm.dop.service.login.dto.UserStatus;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static ee.hm.dop.utils.ConfigurationProperties.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class HaridService {

    private static Logger logger = LoggerFactory.getLogger(HaridService.class);

    @Inject
    private Configuration configuration;
    @Inject
    private Client client;
    @Inject
    private LoginService loginService;
    private HmacUtils hmacUtils;

    @Inject
    public void postConstruct() {
        postConstruct(configuration.getString(HARID_CLIENT_SECRET));
    }

    void postConstruct(String secret) {
        hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secret);
    }

    public UserStatus authenticate(String token) {
        HarIdUser harIdUser = getHaridUser(token);
        if (isBlank(harIdUser.getIdCode())) {
//            return UserStatus.missingHarIdCode();
        }
        return loginService.login(harIdUser.getIdCode(), harIdUser.getFirstName(), harIdUser.getLastName(), LoginFrom.HAR_ID);
    }

    private HarIdUser getHaridUser(String token) {
        Response response = client.target(getUserDataUrl())
                .queryParam("token", token)
                .queryParam("client_id", getClientId())
                .queryParam("signature", hmacUtils.hmacHex(token))
                .request()
                .accept(MediaType.APPLICATION_JSON).get();
        return response.readEntity(HarIdUser.class);
    }

    public String getClientId() {
        return configuration.getString(HARID_CLIENT_ID);
    }

    private String getUserDataUrl() {
        return configuration.getString(HARID_URL_GENERALDATA);
    }

    public String getAuthorizationUrl() {
        return configuration.getString(HARID_URL_AUTHORIZE);
    }
}
