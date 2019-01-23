package ee.hm.dop.rest;

import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.KeyCompetence;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LicenseType;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.enums.ReportingReasonEnum;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.metadata.CrossCurricularThemeService;
import ee.hm.dop.service.metadata.KeyCompetenceService;
import ee.hm.dop.service.metadata.LanguageService;
import ee.hm.dop.service.metadata.LicenseTypeService;
import ee.hm.dop.service.metadata.MaterialMetadataService;
import ee.hm.dop.service.metadata.ResourceTypeService;
import ee.hm.dop.service.metadata.TargetGroupService;
import ee.hm.dop.service.metadata.TaxonService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("learningMaterialMetadata")
public class LearningMaterialMetadataResource extends BaseResource{

    public static final int MAX_AGE_120 = 120;
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
    public ResponseEntity<List<EducationalContext>> getEducationalContext() {
        List<EducationalContext> taxons = taxonService.getAllEducationalContext();
        if (taxons == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        CacheControl cacheControl = CacheControl.maxAge(120, TimeUnit.SECONDS);
        return ResponseEntity.ok().cacheControl(cacheControl).body(taxons);
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
