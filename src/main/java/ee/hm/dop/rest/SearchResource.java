package ee.hm.dop.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.CrossCurricularThemeService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.ResourceTypeService;
import ee.hm.dop.service.SearchService;
import ee.hm.dop.service.TaxonService;

@Path("search")
public class SearchResource {

    @Inject
    private SearchService searchService;

    @Inject
    private TaxonService taxonService;

    @Inject
    private LanguageService languageService;

    @Inject
    private ResourceTypeService resourceTypeService;

    @Inject
    private CrossCurricularThemeService crossCurricularThemeService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult search(@QueryParam("q") String query, @QueryParam("start") Long start, //
            @QueryParam("taxon") Long taxonId, //
            @QueryParam("paid") Boolean paid, //
            @QueryParam("type") String type, //
            @QueryParam("language") String languageCode, //
            @QueryParam("targetGroup") List<TargetGroup> targetGroups, //
            @QueryParam("resourceType") String resourceTypeName, //
            @QueryParam("specialEducation") Boolean isSpecialEducation, //
            @QueryParam("issuedFrom") Integer issuedFrom, //
            @QueryParam("crossCurricularTheme") Long crossCurricularThemeId) {

        Taxon taxon = taxonService.getTaxonById(taxonId);
        Language language = languageService.getLanguage(languageCode);
        ResourceType resourceType = resourceTypeService.getResourceTypeByName(resourceTypeName);
        CrossCurricularTheme crossCurricularTheme = crossCurricularThemeService
                .getCrossCurricularThemeById(crossCurricularThemeId);

        if (paid == null) {
            paid = true;
        }

        if (isSpecialEducation == null) {
            isSpecialEducation = false;
        }

        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxon(taxon);
        searchFilter.setPaid(paid);
        searchFilter.setType(type);
        searchFilter.setLanguage(language);
        searchFilter.setTargetGroups(targetGroups);
        searchFilter.setResourceType(resourceType);
        searchFilter.setSpecialEducation(isSpecialEducation);
        searchFilter.setIssuedFrom(issuedFrom);
        searchFilter.setCrossCurricularTheme(crossCurricularTheme);

        if (start == null) {
            return searchService.search(query, searchFilter);
        } else {
            return searchService.search(query, start, searchFilter);
        }
    }

}
