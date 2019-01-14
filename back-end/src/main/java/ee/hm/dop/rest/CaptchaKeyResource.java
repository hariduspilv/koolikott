package ee.hm.dop.rest;

import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import static ee.hm.dop.utils.ConfigurationProperties.CAPTCHA_KEY;

@Path("/captcha")
public class CaptchaKeyResource extends BaseResource{

    @Inject
    private Configuration configuration;

    @GET
    public String getKey() {
        return configuration.getString(CAPTCHA_KEY);
    }
}
