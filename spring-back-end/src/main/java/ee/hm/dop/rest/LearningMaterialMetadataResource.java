package ee.hm.dop.rest;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReportingReasonEnum;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.metadata.MaterialMetadataService;
import ee.hm.dop.service.metadata.*;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.List;

@RestController
@RequestMapping("learningMaterialMetadata")
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

    @GetMapping(value = "educationalContext", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getEducationalContext() {
        List<EducationalContext> taxons = taxonService.getAllEducationalContext();
        if (taxons != null) {
//            todo why is here 2 min cache?
            return Response.ok(taxons).header(HttpHeaders.CACHE_CONTROL, MAX_AGE_120).build();
        }
        return Response.status(HttpURLConnection.HTTP_NOT_FOUND).build();
    }

    @GetMapping("taxon")
    public Taxon getTaxon(@RequestParam("taxonId") Long taxonId) {
        return taxonService.getTaxonById(taxonId);
    }

    @GetMapping("language")
    public List<Language> getAllLanguages() {
        return languageService.getAll();
    }

    @GetMapping("targetGroup")
    public List<TargetGroup> getTargetGroups() {
        return targetGroupService.getValues();
    }

    @GetMapping("resourceType")
    public List<ResourceType> getAllResourceTypes() {
        return resourceTypeService.getAllResourceTypes();
    }

    @GetMapping("resourceType/used")
    public List<ResourceType> getUsedResourceTypes() {
        return resourceTypeService.getUsedResourceTypes();
    }

    @GetMapping("licenseType")
    public List<LicenseType> getAllLicenseTypes() {
        return licenseTypeService.getAllLicenseTypes();
    }

    @GetMapping("crossCurricularTheme")
    public List<CrossCurricularTheme> getAllCrossCurricularThemes() {
        return crossCurricularThemeService.getAllCrossCurricularThemes();
    }

    @GetMapping("keyCompetence")
    public List<KeyCompetence> getAllCompetences() {
        return keyCompetenceService.getAllKeyCompetences();
    }

    @GetMapping("usedLanguages")
    public List<Language> getUsedLanguages() {
        return materialMetadataService.getLanguagesUsedInMaterials();
    }

    @GetMapping("learningObjectReportingReasons")
    public List<ReportingReasonEnum> learningObjectReportingReasonsModal() {
        return ReportingReasonEnum.learningObjectReportingReasonsModal();
    }

    @GetMapping("tagReportingReasons")
    public List<ReportingReasonEnum> tagReportingReasons() {
        return ReportingReasonEnum.tagReportingReasons();
    }

    @GetMapping("commentReportingReasons")
    public List<ReportingReasonEnum> commentReportingReasons() {
        return ReportingReasonEnum.commentReportingReasons();
    }
}
