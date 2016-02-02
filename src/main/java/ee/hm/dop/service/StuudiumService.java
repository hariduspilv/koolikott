package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.STUUDIUM_CLIENT_ID;
import static ee.hm.dop.utils.ConfigurationProperties.STUUDIUM_URL_AUTHORIZE;

import javax.inject.Inject;

import org.apache.commons.configuration.Configuration;

public class StuudiumService {

    @Inject
    private Configuration configuration;

    public String getAuthorizationUrl() {
        return configuration.getString(STUUDIUM_URL_AUTHORIZE);
    }

    public String getClientId() {
        return configuration.getString(STUUDIUM_CLIENT_ID);
    }

}
