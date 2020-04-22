package ee.hm.dop.rest;

import ee.hm.dop.dao.ReducedLearningObjectDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.service.content.MaterialGetter;
import ee.hm.dop.service.content.PortfolioGetter;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.useractions.UserFavoriteService;
import ee.hm.dop.service.useractions.UserService;
import ee.hm.dop.utils.NumberUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.google.common.primitives.Ints.min;
import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
@RequestMapping("learningObject")
public class
LearningObjectResource extends BaseResource {

    @Inject
    private TagService tagService;
    @Inject
    private UserFavoriteService userFavoriteService;
    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private PortfolioGetter portfolioGetter;
    @Inject
    private MaterialGetter materialGetter;
    @Inject
    private UserService userService;
    @Inject
    private ReducedLearningObjectDao reducedLearningObjectDao;

    @PutMapping("{learningObject}/tags")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject addTag(@PathVariable("learningObject") Long learningObject, @RequestBody Tag newTag) {
        return tagService.addRegularTag(learningObject, newTag, getLoggedInUser());
    }

    @GetMapping("favorite")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR, RoleString.RESTRICTED})
    public UserFavorite hasSetFavorite(@RequestParam("id") Long id) {
        return userFavoriteService.hasFavorited(id, getLoggedInUser());
    }

    @PostMapping("favorite")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public UserFavorite favoriteLearningObject(@RequestBody LearningObject learningObject) {
        return userFavoriteService.addUserFavorite(learningObject, getLoggedInUser());
    }

    @DeleteMapping("favorite")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void removeUserFavorite(@RequestParam("id") long id) {
        userFavoriteService.removeUserFavorite(id, getLoggedInUser());
    }

    @PostMapping("favorite/delete")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void removeUserFavorite2(@RequestBody LearningObjectMiniDto loDto) {
        userFavoriteService.removeUserFavorite(loDto.getId(), getLoggedInUser());
    }

    @GetMapping("usersFavorite")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR, RoleString.RESTRICTED})
    public SearchResult getUsersFavorites(@RequestParam(value = "start", defaultValue = "0") int start,
                                          @RequestParam(value = "maxResults", defaultValue = "0") int maxResults) {
        SearchResult result = userFavoriteService.getUserFavoritesSearchResult(getLoggedInUser(), 0, 2147483647);
        result.getItems().sort(Comparator.comparing(Searchable::getType).reversed());
        int stop = min(start + NumberUtils.zvl(maxResults, 12), result.getItems().size());
        result.setItems(result.getItems().subList(start, stop));
        result.setStart(start);
        return result;
    }

    @GetMapping("usersFavorite/count")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR, RoleString.RESTRICTED})
    public Long getUsersFavoritesCount() {
        return userFavoriteService.getUserFavoritesSize(getLoggedInUser());
    }

    @PostMapping("increaseViewCount")
    public void increaseViewCount(@RequestBody LearningObjectMiniDto learningObjectMiniDto) {
        learningObjectService.incrementViewCount(learningObjectMiniDto.convert());
    }

    @GetMapping("showUnreviewed")
    public boolean showUnreviewedMessage(@RequestParam("id") Long id) {
        return learningObjectService.showUnreviewed(id, getLoggedInUser());
    }


    @GetMapping("getByCreatorAllReducedLearningObjects")
    public SearchResult getByCreatorAllReducedLearningObjects(@RequestParam("username") String username,
                                                              @RequestParam(value = "start", defaultValue = "0") int start,
                                                              @RequestParam(value = "maxResults", defaultValue = "0") int maxResults) {
        User creator = getValidCreator(username);
        if (creator == null) throw notFound();

        int size = (int) (materialGetter.getByCreatorSize(creator) + portfolioGetter.getCountByCreator(creator));

        List<Searchable> searchableList = new ArrayList<>(learningObjectService.getReducedLOsByCreator(creator, start, maxResults));

        return new SearchResult(searchableList, size, start);
    }

    @GetMapping("getByCreatorAllLearningObjectsCount")
    public Long getByCreator(@RequestParam("username") String username) {
        User creator = getValidCreator(username);
        if (creator == null) throw notFound();

        return materialGetter.getByCreatorSize(creator) + portfolioGetter.getCountByCreator(creator);
    }

    @GetMapping("getAllLearningObjectsByCreator")
    public List<LearningObject> getAllByCreator(@RequestParam("username") String username) {
        User creator = getValidCreator(username);
        if (creator == null) throw notFound();
        return learningObjectService.getAllByCreator(creator);
    }


    private User getValidCreator(@RequestParam("username") String username) {
        if (isBlank(username)) throw badRequest("Username parameter is mandatory");
        return userService.getUserByUsername(username);
    }
}
