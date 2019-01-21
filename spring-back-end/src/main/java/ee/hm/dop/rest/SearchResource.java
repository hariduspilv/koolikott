package ee.hm.dop.rest;

import ee.hm.dop.model.*;
import ee.hm.dop.service.metadata.*;
import ee.hm.dop.service.solr.SearchService;
import ee.hm.dop.service.useractions.UserLikeService;
import ee.hm.dop.utils.NumberUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static ee.hm.dop.service.solr.SearchCommandBuilder.UNIQUE_KEYS;

@RestController
@RequestMapping("search")
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

    @GetMapping
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult search(@RequestParam("q") String query,
                               @RequestParam("start") Long start,
                               @RequestParam("taxon") List<Long> taxonIds,
                               @RequestParam(value = "paid", defaultValue = "true") boolean paid,
                               @RequestParam("type") String type,
                               @RequestParam("language") String languageCode,
                               @RequestParam("targetGroup") List<String> targetGroupNames,
                               @RequestParam("resourceType") String resourceTypeName,
                               @RequestParam("specialEducation") boolean isSpecialEducation,
                               @RequestParam("issuedFrom") Integer issuedFrom,
                               @RequestParam("crossCurricularTheme") List<Long> crossCurricularThemeIds,
                               @RequestParam("keyCompetence") List<Long> keyCompetenceIds,
                               @RequestParam("curriculumLiterature") boolean isCurriculumLiterature,
                               @RequestParam("sort") String sort,
                               @RequestParam("sortDirection") String sortDirection,
                               @RequestParam("limit") Long limit,
                               @RequestParam("creator") Long creator,
                               @RequestParam("private") boolean myPrivates,
                               @RequestParam("isORSearch") boolean isORSearch,
                               @RequestParam("recommended") boolean recommended,
                               @RequestParam("favorites") boolean favorites,
                               @RequestParam("excluded") List<Long> excluded,
                               @RequestParam("isGrouped") boolean isGrouped) {

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
        searchFilter.setSort(SortType.getByValue(sort));
        searchFilter.setSortDirection(SortDirection.getByValue(sortDirection));
        searchFilter.setCreator(creator);
        searchFilter.setRequestingUser(getLoggedInUser());
        searchFilter.setMyPrivates(myPrivates);
        searchFilter.setExcluded(excluded);
        searchFilter.setRecommended(recommended);
        searchFilter.setFavorites(favorites);
        searchFilter.setSearchType(isORSearch ? "OR" : "AND");
        searchFilter.setGrouped(isGrouped);
        searchFilter.setFieldSpecificSearch(isQueryFieldSpecific(query));
        return searchService.search(query, NumberUtils.nvl(start, 0L), limit, searchFilter);
    }

    private boolean isQueryFieldSpecific(String queryInput) {
        return queryInput != null && UNIQUE_KEYS.stream().anyMatch((group) -> queryInput.startsWith(group + ":"));
    }

    @GetMapping
    @RequestMapping("mostLiked")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Searchable> getMostLiked(@RequestParam("maxResults") int maxResults) {
        return userLikeService.getMostLiked(maxResults);
    }
}
