package ee.hm.dop.service.login;

import ee.hm.dop.model.ekool.EkoolToken;
import ee.hm.dop.model.ekool.Person;
import ee.hm.dop.service.login.dto.UserStatus;
import org.apache.commons.configuration2.Configuration;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import java.nio.charset.StandardCharsets;

import static ee.hm.dop.utils.ConfigurationProperties.*;
import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED_TYPE;
import static org.apache.xml.security.utils.Base64.encode;

public class EkoolService {

    @Inject
    private Configuration configuration;
    @Inject
    private Client client;
    @Inject
    private LoginService loginService;

    public String getAuthorizationUrl() {
        return configuration.getString(EKOOL_URL_AUTHORIZE);
    }

    public String getClientId() {
        return configuration.getString(EKOOL_CLIENT_ID);
    }

    public UserStatus authenticate(String code, String redirectUrl) {
        EkoolToken ekoolToken = getEkoolToken(code, redirectUrl);
        Person person = getPerson(ekoolToken);
        return loginService.login(person.getIdCode(), person.getFirstName(), person.getLastName());
    }

    private Person getPerson(EkoolToken ekoolToken) {
        return client.target(getUserDataUrl()).request()
                .header("Authorization", "Bearer " + ekoolToken.getAccessToken())
                .header("Content-type", "application/x-www-form-urlencoded")
                .get().readEntity(Person.class);
    }

    private EkoolToken getEkoolToken(String code, String redirectUrl) {
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUrl);
        params.add("code", code);

        return client.target(getEkoolTokenUrl()).request()
                .header("Authorization", "Basic " + generateAuthHeaderHash())
                .post(Entity.entity(params, APPLICATION_FORM_URLENCODED_TYPE))
                .readEntity(EkoolToken.class);
    }

    private String getEkoolTokenUrl() {
        return configuration.getString(EKOOL_URL_TOKEN);
    }

    private String getUserDataUrl() {
        return configuration.getString(EKOOL_URL_GENERALDATA);
    }

    private String getClientSecret() {
        return configuration.getString(EKOOL_CLIENT_SECRET);
    }

    private String generateAuthHeaderHash() {
        String authHeader = format("%s:%s", getClientId(), getClientSecret());
        return encode(authHeader.getBytes(StandardCharsets.UTF_8));
    }

}
