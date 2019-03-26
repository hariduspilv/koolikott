package ee.hm.dop.rest;

import ee.hm.dop.config.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static ee.hm.dop.utils.ConfigurationProperties.CAPTCHA_KEY;

@RestController
@RequestMapping("/captcha")
public class CaptchaKeyResource extends BaseResource {

    @Inject
    private Configuration configuration;

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String getKey() {
        return configuration.getString(CAPTCHA_KEY);
    }
}
