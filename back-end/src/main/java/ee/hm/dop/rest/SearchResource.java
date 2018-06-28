package ee.hm.dop.rest;

import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.SortDirection;
import ee.hm.dop.service.metadata.*;
import ee.hm.dop.service.solr.SearchService;
import ee.hm.dop.service.useractions.UserLikeService;
import ee.hm.dop.utils.NumberUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
    @Inject
    private TargetGroupService targetGroupService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult search(@QueryParam("q") String query,
                               @QueryParam("start") Long start,
                               @QueryParam("taxon") List<Long> taxonIds,
                               @DefaultValue("true") @QueryParam("paid") boolean paid,
                               @QueryParam("type") String type,
                               @QueryParam("language") String languageCode,
                               @QueryParam("targetGroup") List<String> targetGroupNames,
                               @QueryParam("resourceType") String resourceTypeName,
                               @QueryParam("specialEducation") boolean isSpecialEducation,
                               @QueryParam("issuedFrom") Integer issuedFrom,
                               @QueryParam("crossCurricularTheme") List<Long> crossCurricularThemeIds,
                               @QueryParam("keyCompetence") List<Long> keyCompetenceIds,
                               @QueryParam("curriculumLiterature") boolean isCurriculumLiterature,
                               @QueryParam("sort") String sort,
                               @QueryParam("sortDirection") String sortDirection,
                               @QueryParam("limit") Long limit,
                               @QueryParam("creator") Long creator,
                               @QueryParam("private") boolean myPrivates,
                               @QueryParam("isORSearch") boolean isORSearch,
                               @QueryParam("recommended") boolean recommended,
                               @QueryParam("favorites") boolean favorites,
                               @QueryParam("excluded") List<Long> excluded,
                               @QueryParam("isGrouped") boolean isGrouped) {

        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(taxonService.getTaxonById(taxonIds));
        searchFilter.setPaid(paid);
        searchFilter.setType(type);
        searchFilter.setLanguage(languageService.getLanguage(languageCode));
        searchFilter.setTargetGroups(targetGroupService.getByName(targetGroupNames));
        searchFilter.setResourceType(resourceTypeService.getResourceTypeByName(resourceTypeName));
        searchFilter.setSpecialEducation(isSpecialEducation);
        searchFilter.setIssuedFrom(issuedFrom);
        searchFilter.setCrossCurricularThemes(crossCurricularThemeService.getCrossCurricularThemeById(crossCurricularThemeIds));
        searchFilter.setKeyCompetences(keyCompetenceService.getKeyCompetenceById(keyCompetenceIds));
        searchFilter.setCurriculumLiterature(isCurriculumLiterature);
        searchFilter.setSort(sort);
        searchFilter.setSortDirection(SortDirection.getByValue(sortDirection));
        searchFilter.setCreator(creator);
        searchFilter.setRequestingUser(getLoggedInUser());
        searchFilter.setMyPrivates(myPrivates);
        searchFilter.setExcluded(excluded);
        searchFilter.setRecommended(recommended);
        searchFilter.setFavorites(favorites);
        searchFilter.setSearchType(isORSearch ? "OR" : "AND");
        searchFilter.setGrouped(isGrouped);
        return searchService.search(query, NumberUtils.nvl(start, 0L), limit, searchFilter);
    }

    @GET
    @Path("mostLiked")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Searchable> getMostLiked(@QueryParam("maxResults") int maxResults) {
        return userLikeService.getMostLiked(maxResults);
    }
}
