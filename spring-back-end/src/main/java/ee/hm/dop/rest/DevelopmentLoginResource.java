package ee.hm.dop.rest;

import ee.hm.dop.model.enums.LoginFrom;
import ee.hm.dop.service.login.LoginService;
import ee.hm.dop.service.login.dto.UserStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;

@RestController
@RequestMapping("dev/")
@Profile("test")
public class DevelopmentLoginResource {

    @Inject
    private LoginService loginService;
    @Inject
    private Environment environment;

    @GetMapping
    @RequestMapping(value = "/login/{idCode}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public UserStatus logIn(@PathVariable("idCode") String idCode) {
        if (Boolean.parseBoolean(environment.getProperty("app.devLogin"))) {
            return loginService.login(idCode, null, null, LoginFrom.DEV);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
