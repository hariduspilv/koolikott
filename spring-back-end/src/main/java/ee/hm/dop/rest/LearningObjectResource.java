package ee.hm.dop.rest;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.Like;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.useractions.UserFavoriteService;
import ee.hm.dop.service.useractions.UserLikeService;
import ee.hm.dop.utils.NumberUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Comparator;

import static com.google.common.primitives.Ints.min;

@RestController
@RequestMapping("learningObject")
public class LearningObjectResource extends BaseResource {

    @Inject
    private TagService tagService;
    @Inject
    private UserFavoriteService userFavoriteService;
    @Inject
    private UserLikeService userLikeService;
    @Inject
    private LearningObjectService learningObjectService;

    @PutMapping("{learningObjectId}/tags")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject addTag(@PathVariable("learningObjectId") Long learningObjectId, @RequestBody Tag newTag) {
        return tagService.addRegularTag(learningObjectId, newTag, getLoggedInUser());
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
    public SearchResult getUsersFavorites(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "maxResults", defaultValue = "0") int maxResults) {
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

    @PostMapping("getUserLike")
    public UserLike getUserLike(@RequestBody LearningObjectMiniDto learningObjectMiniDto) {
        return userLikeService.getUserLike(learningObjectMiniDto.convert(), getLoggedInUser());
    }

    @PostMapping("removeUserLike")
    public void removeUserLike(@RequestBody LearningObjectMiniDto learningObjectMiniDto) {
        userLikeService.removeUserLike(learningObjectMiniDto.convert(), getLoggedInUser());
    }

    @PostMapping("like")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void likeMaterial(@RequestBody LearningObjectMiniDto learningObjectMiniDto) {
        userLikeService.addUserLike(learningObjectMiniDto.convert(), getLoggedInUser(), Like.LIKE);
    }

    @PostMapping("dislike")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void dislikeMaterial(@RequestBody LearningObjectMiniDto learningObjectMiniDto) {
        userLikeService.addUserLike(learningObjectMiniDto.convert(), getLoggedInUser(), Like.DISLIKE);
    }

    @PostMapping("increaseViewCount")
    public void increaseViewCount(@RequestBody LearningObjectMiniDto learningObjectMiniDto) {
        learningObjectService.incrementViewCount(learningObjectMiniDto.convert());
    }

    @GetMapping("showUnreviewed")
    public boolean showUnreviewedMessage(@RequestParam("id") Long id) {
        return learningObjectService.showUnreviewed(id, getLoggedInUser());
    }
}
