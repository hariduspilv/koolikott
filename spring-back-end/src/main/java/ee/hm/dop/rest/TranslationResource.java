package ee.hm.dop.rest;

import ee.hm.dop.model.LandingPageObject;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.metadata.TranslationService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("translation")
public class TranslationResource {

    @Inject
    private TranslationService translationService;

    @GetMapping
    public Map<String, String> getTranslation(@RequestParam("lang") String language) {
        return translationService.getTranslationsFor(language);
    }

    @GetMapping
    @RequestMapping("landingPage")
    public ResponseEntity<LandingPageObject> getTranslations() {
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(0, TimeUnit.SECONDS)).body(translationService.getTranslations());
    }

    @GetMapping
    @RequestMapping("landingPage/admin")
    public LandingPageObject getTranslationsForAdmin() {
        return translationService.getTranslations();
    }

    @PostMapping
    @RequestMapping("update")
    @Secured(RoleString.ADMIN)
    public void update(@RequestBody LandingPageObject landingPageObject) {
        translationService.update(landingPageObject);
    }

}
