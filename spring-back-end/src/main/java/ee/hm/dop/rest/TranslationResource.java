package ee.hm.dop.rest;

import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.LandingPageObject;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.metadata.TranslationService;
import org.apache.http.HttpHeaders;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static ee.hm.dop.rest.LearningMaterialMetadataResource.MAX_AGE_120;

@RestController
@RequestMapping("translation")
public class TranslationResource {

    @Inject
    private TranslationService translationService;

    @GetMapping
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> getTranslation(@RequestParam("lang") String language) {
        return translationService.getTranslationsFor(language);
    }

    @GetMapping
    @RequestMapping("landingPage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTranslations() {
        return Response.ok(translationService.getTranslations()).header(HttpHeaders.CACHE_CONTROL, 0).build();
    }


    @GetMapping
    @RequestMapping("landingPage/admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public LandingPageObject getTranslationsForAdmin() {
        return translationService.getTranslations();
    }

    @PostMapping
    @RequestMapping("update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(RoleString.ADMIN)
    public void update(LandingPageObject landingPageObject) {
        translationService.update(landingPageObject);
    }

}
