package ee.hm.dop.rest;

import org.apache.commons.configuration2.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import static ee.hm.dop.utils.ConfigurationProperties.CAPTCHA_KEY;

@RestController
@RequestMapping("/captcha")
public class CaptchaKeyResource extends BaseResource{

    @Inject
    private Configuration configuration;

    @GetMapping
    public String getKey() {
        return configuration.getString(CAPTCHA_KEY);
    }
}
