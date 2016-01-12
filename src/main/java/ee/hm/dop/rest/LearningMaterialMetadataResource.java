package ee.hm.dop.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.KeyCompetence;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LicenseType;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.CrossCurricularThemeService;
import ee.hm.dop.service.KeyCompetenceService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.LicenseTypeService;
import ee.hm.dop.service.ResourceTypeService;
import ee.hm.dop.service.TaxonService;

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("educationalContext")
    public List<EducationalContext> getEducationalContext() {
        return taxonService.getAllEducationalContext();
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
    public TargetGroup[] getTargetGroups() {
        return TargetGroup.values();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("resourceType")
    public List<ResourceType> getAllResourceTypes() {
        return resourceTypeService.getAllResourceTypes();
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

}
