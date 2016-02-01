package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.EKOOL_CLIENT_ID;
import static ee.hm.dop.utils.ConfigurationProperties.EKOOL_CLIENT_SECRET;
import static ee.hm.dop.utils.ConfigurationProperties.EKOOL_URL_AUTHORIZE;
import static ee.hm.dop.utils.ConfigurationProperties.EKOOL_URL_GENERALDATA;
import static ee.hm.dop.utils.ConfigurationProperties.EKOOL_URL_TOKEN;
import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED_TYPE;
import static org.apache.xml.security.utils.Base64.encode;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.configuration.Configuration;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.ekool.EkoolToken;
import ee.hm.dop.model.ekool.Person;

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

    public AuthenticatedUser authenticate(String code, String redirectUrl) {

        EkoolToken ekoolToken = getEkoolToken(code, redirectUrl);
        Person person = getPerson(ekoolToken);
        return loginService.logIn(person.getIdCode(), person.getFirstName(), person.getLastName());
    }

    private Person getPerson(EkoolToken ekoolToken) {
        MultivaluedMap<String, String> params = new MultivaluedStringMap();
        params.add("access_token", ekoolToken.getAccessToken());

        Person person = client.target(getUserDataUrl()).request() //
                .post(Entity.entity(params, APPLICATION_FORM_URLENCODED_TYPE)) //
                .readEntity(Person.class);
        return person;
    }

    private EkoolToken getEkoolToken(String code, String redirectUrl) {
        MultivaluedMap<String, String> tokenRequestParams = new MultivaluedStringMap();
        tokenRequestParams.add("grant_type", "authorization_code");
        tokenRequestParams.add("redirect_uri", redirectUrl);
        tokenRequestParams.add("code", code);

        Entity<MultivaluedMap<String, String>> entity = Entity.entity(tokenRequestParams,
                APPLICATION_FORM_URLENCODED_TYPE);

        EkoolToken ekoolToken = client.target(getEkoolTokenUrl()).request()
                .header("Authorization", "Basic " + generateAuthHeaderHash()) //
                .post(entity) //
                .readEntity(EkoolToken.class);
        return ekoolToken;
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
        return encode(authHeader.getBytes());
    }

}
