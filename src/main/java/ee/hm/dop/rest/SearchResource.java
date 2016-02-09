package ee.hm.dop.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.KeyCompetence;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.CrossCurricularThemeService;
import ee.hm.dop.service.KeyCompetenceService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.ResourceTypeService;
import ee.hm.dop.service.SearchService;
import ee.hm.dop.service.TaxonService;
import ee.hm.dop.service.UserLikeService;

@Path("search")
public class SearchResource extends BaseResource {

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

    @Inject
    private KeyCompetenceService keyCompetenceService;

    @Inject
    private UserLikeService userLikeService;

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
            @QueryParam("crossCurricularTheme") Long crossCurricularThemeId, //
            @QueryParam("keyCompetence") Long keyCompetenceId, //
            @QueryParam("curriculumLiterature") Boolean isCurriculumLiterature, //
            @QueryParam("sort") String sort, //
            @QueryParam("sortDirection") String sortDirection, //
            @QueryParam("limit") Long limit) {

        Taxon taxon = taxonService.getTaxonById(taxonId);
        Language language = languageService.getLanguage(languageCode);
        ResourceType resourceType = resourceTypeService.getResourceTypeByName(resourceTypeName);
        CrossCurricularTheme crossCurricularTheme = crossCurricularThemeService
                .getCrossCurricularThemeById(crossCurricularThemeId);
        KeyCompetence keyCompetence = keyCompetenceService.getKeyCompetenceById(keyCompetenceId);

        if (paid == null) {
            paid = true;
        }

        if (isSpecialEducation == null) {
            isSpecialEducation = false;
        }

        if (start == null) {
            start = 0L;
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
        searchFilter.setKeyCompetence(keyCompetence);
        searchFilter.setCurriculumLiterature(isCurriculumLiterature);
        searchFilter.setSort(sort);
        searchFilter.setSortDirection(SearchFilter.SortDirection.getByValue(sortDirection));

        return searchService.search(query, start, limit, searchFilter, getLoggedInUser());
    }

    @GET
    @Path("mostLiked")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Searchable> getMostLiked(@PathParam("maxResults") int maxResults) {
        return userLikeService.getMostLiked(maxResults);
    }

}
