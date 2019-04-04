package ee.hm.dop.service.login;

import ee.hm.dop.model.enums.LoginFrom;
import ee.hm.dop.model.harid.HarIdCode;
import ee.hm.dop.model.harid.HarIdUser;
import ee.hm.dop.service.login.dto.UserStatus;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;

import static ee.hm.dop.utils.ConfigurationProperties.HARID_CLIENT_ID;
import static ee.hm.dop.utils.ConfigurationProperties.HARID_CLIENT_SECRET;
import static ee.hm.dop.utils.ConfigurationProperties.HARID_URL_AUTHORIZE;
import static ee.hm.dop.utils.ConfigurationProperties.HARID_URL_GENERALDATA;
import static ee.hm.dop.utils.ConfigurationProperties.HARID_URL_TOKEN;
import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED_TYPE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.xml.security.utils.Base64.encode;

public class HaridService {

    private static Logger logger = LoggerFactory.getLogger(HaridService.class);

    @Inject
    private Configuration configuration;
    @Inject
    private Client client;
    @Inject
    private LoginService loginService;
    private HmacUtils hmacUtils;

//    @Inject
//    public void postConstruct() {
//        postConstruct(configuration.getString(HARID_CLIENT_SECRET));
//    }

    void postConstruct(String secret) {
        hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secret);
    }

    public UserStatus authenticate(String code,String redirectUrl) {
        HarIdCode harIdCode = getHarIdCode(code,redirectUrl);
        logger.info("harId accessToken: " + harIdCode.getAccessToken());
        logger.info("harId accessredirectUrl!!!! : " + redirectUrl);

        HarIdUser harIdUser = getHaridUser(harIdCode);
        if (isBlank(harIdUser.getIdCode())) {
            logger.info("HarIdUser doesnt have idcode");
            return UserStatus.missingHarIdCode();
        }
        return loginService.login(harIdUser.getIdCode(), harIdUser.getFirstName(), harIdUser.getLastName(), LoginFrom.HAR_ID);
    }

    private HarIdCode getHarIdCode(String code, String redirectUrl) {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUrl);
        params.add("code", code);

        Response response = client.target(getHarIdTokenUrl())
                .request()
                .header("Authorization", "Basic " + generateAuthHeaderHash())
                .post(Entity.entity(params, APPLICATION_FORM_URLENCODED_TYPE));
        logAsString("getCode", response);
        return response.readEntity(HarIdCode.class);
    }

    private HarIdUser getHaridUser(HarIdCode code) {
        Response response = client.target(getUserDataUrl())
                .request()
                .header("Authorization", "Bearer " + code.getAccessToken())
                .header("Content-type", "application/x-www-form-urlencoded")
                .accept(MediaType.APPLICATION_JSON)
                .get();
        logAsString("getPerson", response);
        logger.info("accesstoken for getting Harid user: " +  code.getAccessToken());
        return response.readEntity(HarIdUser.class);
    }

    private String getHarIdTokenUrl() {
        return configuration.getString(HARID_URL_TOKEN);
    }

    public String getAuthorizationUrl() {
        return configuration.getString(HARID_URL_AUTHORIZE);
    }

    public String getClientId() {
        return configuration.getString(HARID_CLIENT_ID);
    }

    private String getUserDataUrl() {
        return configuration.getString(HARID_URL_GENERALDATA);
    }

    private String getClientSecret() {
        return configuration.getString(HARID_CLIENT_SECRET);
    }

    private String generateAuthHeaderHash() {
        String authHeader = format("%s:%s", getClientId(), getClientSecret());
        return encode(authHeader.getBytes(StandardCharsets.UTF_8));
    }

    private void logAsString(String reason, Response response) {
            response.bufferEntity();
            logger.info(reason + " " + response.readEntity(String.class));
    }
}
