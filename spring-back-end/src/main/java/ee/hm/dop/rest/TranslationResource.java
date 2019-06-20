package ee.hm.dop.rest;

import ee.hm.dop.model.LandingPageObject;
import ee.hm.dop.model.TranslationDto;
import ee.hm.dop.model.TranslationsDto;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.metadata.TranslationService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
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

    @GetMapping(value = "getTranslationForTranslationObject",produces = MediaType.TEXT_PLAIN_VALUE)
    public String getTranslationForLanguage(@RequestParam("translationKey") String translationKey, @RequestParam("languageKey") String languageKey) {
        return translationService.getTranslations(translationKey, languageKey);
    }

    @GetMapping(value = "getTranslationsForTranslationObject")
    public List<String> getTranslationsForLanguage(@RequestParam("translationKey") String translationKey) {
        return translationService.getTranslations(translationKey);
    }

    @GetMapping(value = "getAllTranslations")
    public List<String> getAllTranslations(@RequestParam("translationKey") String translationKey) {
        return translationService.getAllTranslations(translationKey);
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

    @PostMapping
    @RequestMapping("updateTranslation")
    @Secured(RoleString.ADMIN)
    public void updateTranslation(@RequestBody TranslationDto translationDto) {
        translationService.updateTranslation(translationDto);
    }

    @PostMapping
    @RequestMapping("updateTranslations")
    @Secured(RoleString.ADMIN)
    public void updateTranslationMultiple(@RequestBody TranslationsDto translationDtos) {
        translationService.updateTranslations(translationDtos);
    }
}
