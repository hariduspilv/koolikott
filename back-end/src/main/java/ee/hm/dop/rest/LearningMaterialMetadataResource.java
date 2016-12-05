package ee.hm.dop.rest;

import java.net.HttpURLConnection;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.*;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.*;
import org.apache.http.HttpHeaders;

@Path("learningMaterialMetadata")
public class LearningMaterialMetadataResource {

    @Inject
    private TaxonService taxonService;

    @Inject
    private LanguageService languageService;

    @Inject
    private ResourceTypeService resourceTypeService;

    @Inject
    private LicenseTypeService licenseTypeService;

    @Inject
    private CrossCurricularThemeService crossCurricularThemeService;

    @Inject
    private KeyCompetenceService keyCompetenceService;

    @Inject
    private MaterialService materialservice;

    @Inject
    private TargetGroupService targetGroupService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("educationalContext")
    public Response getEducationalContext() {
        List<EducationalContext> taxons = taxonService.getAllEducationalContext();
        if (taxons != null) {
            return Response.ok(taxons).header(HttpHeaders.CACHE_CONTROL, "max-age=120").build();
        }

        return Response.status(HttpURLConnection.HTTP_NOT_FOUND).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("taxon")
    public Taxon getTaxon(@QueryParam("taxonId") Long taxonId) {
        return taxonService.getTaxonById(taxonId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("language")
    public List<Language> getAllLanguages() {
        return languageService.getAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("targetGroup")
    public List<TargetGroup> getTargetGroups() {
        return targetGroupService.getValues();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("resourceType")
    public List<ResourceType> getAllResourceTypes() {
        return resourceTypeService.getAllResourceTypes();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("resourceType/used")
    public List<ResourceType> getUsedResourceTypes() {
        return resourceTypeService.getUsedResourceTypes();
    }

    @GET
    @Path("licenseType")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LicenseType> getAllLicenseTypes() {
        return licenseTypeService.getAllLicenseTypes();
    }

    @GET
    @Path("crossCurricularTheme")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CrossCurricularTheme> getAllCrossCurricularThemes() {
        return crossCurricularThemeService.getAllCrossCurricularThemes();
    }

    @GET
    @Path("keyCompetence")
    @Produces(MediaType.APPLICATION_JSON)
    public List<KeyCompetence> getAllCompetences() {
        return keyCompetenceService.getAllKeyCompetences();
    }

    @GET
    @Path("usedLanguages")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Language> getUsedLanguages() {
        return materialservice.getLanguagesUsedInMaterials();
    }

}
