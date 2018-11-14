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

import static ee.hm.dop.rest.LearningMaterialMetadataResource.MAX_AGE_120;

@Path("translation")
public class TranslationResource {

    @Inject
    private TranslationService translationService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> getTranslation(@QueryParam("lang") String language) {
        return translationService.getTranslationsFor(language);
    }

    @GET
    @Path("landingPage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTranslations() {
        return Response.ok(translationService.getTranslations()).header(HttpHeaders.CACHE_CONTROL, MAX_AGE_120).build();
    }


    @GET
    @Path("landingPage/admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public LandingPageObject getTranslationsForAdmin() {
        return translationService.getTranslations();
    }

    @POST
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(RoleString.ADMIN)
    public void update(LandingPageObject landingPageObject) {
        translationService.update(landingPageObject);
    }

}
