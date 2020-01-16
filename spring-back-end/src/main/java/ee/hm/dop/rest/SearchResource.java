package ee.hm.dop.rest;

import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.SortDirection;
import ee.hm.dop.model.SortType;
import ee.hm.dop.service.metadata.CrossCurricularThemeService;
import ee.hm.dop.service.metadata.KeyCompetenceService;
import ee.hm.dop.service.metadata.LanguageService;
import ee.hm.dop.service.metadata.ResourceTypeService;
import ee.hm.dop.service.metadata.TargetGroupService;
import ee.hm.dop.service.metadata.TaxonService;
import ee.hm.dop.service.solr.SearchService;
import ee.hm.dop.service.useractions.UserLikeService;
import ee.hm.dop.utils.NumberUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
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
    public SearchResult search(@RequestParam(value = "q", required = false) String query,
                               @RequestParam(value = "start", required = false) Long start,
                               @RequestParam(value = "taxon", required = false) List<Long> taxonIds,
                               @RequestParam(value = "paid", defaultValue = "true") boolean paid,
                               @RequestParam(value = "type", required = false) String type,
                               @RequestParam(value = "language", required = false) String languageCode,
                               @RequestParam(value = "targetGroup", required = false) List<String> targetGroupNames,
                               @RequestParam(value = "resourceType", required = false) String resourceTypeName,
                               @RequestParam(value = "specialEducation", required = false) boolean isSpecialEducation,
                               @RequestParam(value = "issuedFrom", required = false) Integer issuedFrom,
                               @RequestParam(value = "crossCurricularTheme", required = false) List<Long> crossCurricularThemeIds,
                               @RequestParam(value = "keyCompetence", required = false) List<Long> keyCompetenceIds,
                               @RequestParam(value = "curriculumLiterature", required = false) boolean isCurriculumLiterature,
                               @RequestParam(value = "sort", required = false) String sort,
                               @RequestParam(value = "sortDirection", required = false) String sortDirection,
                               @RequestParam(value = "limit", required = false) Long limit,
                               @RequestParam(value = "creator", required = false) Long creator,
                               @RequestParam(value = "private", required = false) boolean myPrivates,
                               @RequestParam(value = "isORSearch", required = false) boolean isORSearch,
                               @RequestParam(value = "recommended", required = false) boolean recommended,
                               @RequestParam(value = "favorites", required = false) boolean favorites,
                               @RequestParam(value = "excluded", required = false) List<Long> excluded,
                               @RequestParam(value = "isGrouped", required = false) boolean isGrouped) {

        SearchFilter searchFilter = new SearchFilter();
        System.out.println("enne taxoneid");
        searchFilter.setTaxons(taxonService.getTaxonById(taxonIds));
        System.out.println("p'rast taxoneid");
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

    public List<Searchable> getMostLiked(@RequestParam("maxResults") int maxResults) {
        return userLikeService.getMostLiked(maxResults);
    }
}
