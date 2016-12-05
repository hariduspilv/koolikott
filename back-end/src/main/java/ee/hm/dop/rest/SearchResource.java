package ee.hm.dop.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.*;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.*;

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
                               @QueryParam("paid") Boolean paid,
                               @QueryParam("type") String type,
                               @QueryParam("language") String languageCode,
                               @QueryParam("targetGroup") List<String> targetGroupNames,
                               @QueryParam("resourceType") String resourceTypeName,
                               @QueryParam("specialEducation") boolean isSpecialEducation,
                               @QueryParam("issuedFrom") Integer issuedFrom,
                               @QueryParam("crossCurricularTheme") Long crossCurricularThemeId,
                               @QueryParam("keyCompetence") Long keyCompetenceId,
                               @QueryParam("curriculumLiterature") Boolean isCurriculumLiterature,
                               @QueryParam("sort") String sort,
                               @QueryParam("sortDirection") String sortDirection,
                               @QueryParam("limit") Long limit,
                               @QueryParam("creator") Long creator,
                               @QueryParam("private") boolean myPrivates) {

        List<Taxon> taxons = taxonIds
                .stream()
                .map(taxonId -> taxonService.getTaxonById(taxonId))
                .collect(Collectors.toList());

        List<TargetGroup> targetGroups = targetGroupNames
                .stream()
                .map(name -> targetGroupService.getByName(name))
                .collect(Collectors.toList());

        Language language = languageService.getLanguage(languageCode);
        ResourceType resourceType = resourceTypeService.getResourceTypeByName(resourceTypeName);
        CrossCurricularTheme crossCurricularTheme = crossCurricularThemeService
                .getCrossCurricularThemeById(crossCurricularThemeId);
        KeyCompetence keyCompetence = keyCompetenceService.getKeyCompetenceById(keyCompetenceId);

        if (paid == null) {
            paid = true;
        }

        if (start == null) {
            start = 0L;
        }

        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setTaxons(taxons);
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
        searchFilter.setCreator(creator);
        searchFilter.setRequestingUser(getLoggedInUser());
        searchFilter.setMyPrivates(myPrivates);

        return searchService.search(query, start, limit, searchFilter);
    }

    @GET
    @Path("mostLiked")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Searchable> getMostLiked(@QueryParam("maxResults") int maxResults) {
        return userLikeService.getMostLiked(maxResults);
    }
}
