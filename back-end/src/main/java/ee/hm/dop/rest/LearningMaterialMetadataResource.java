package ee.hm.dop.rest;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReportingReasonEnum;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.content.MaterialMetadataService;
import ee.hm.dop.service.metadata.*;
import org.apache.http.HttpHeaders;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.List;

@Path("learningMaterialMetadata")
public class LearningMaterialMetadataResource extends BaseResource{

    public static final String MAX_AGE_120 = "max-age=120";
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
    private TargetGroupService targetGroupService;
    @Inject
    private MaterialMetadataService materialMetadataService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("educationalContext")
    public Response getEducationalContext() {
        List<EducationalContext> taxons = taxonService.getAllEducationalContext();
        if (taxons != null) {
//            todo why is here 2 min cache?
            return Response.ok(taxons).header(HttpHeaders.CACHE_CONTROL, MAX_AGE_120).build();
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
        return materialMetadataService.getLanguagesUsedInMaterials();
    }

    @GET
    @Path("reportingReasons")
    @Produces(MediaType.APPLICATION_JSON)
    public ReportingReasonEnum[] getReportingReasons() {
        return ReportingReasonEnum.values();
    }
}
