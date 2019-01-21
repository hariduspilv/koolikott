package ee.hm.dop.rest;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.Like;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.service.content.dto.TagDTO;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.useractions.UserFavoriteService;
import ee.hm.dop.service.useractions.UserLikeService;
import ee.hm.dop.utils.NumberUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Comparator;

import static com.google.common.primitives.Ints.min;

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

    @PutMapping
    @RequestMapping("{learningObjectId}/tags")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public LearningObject addTag(@PathVariable("learningObjectId") Long learningObjectId, Tag newTag) {
        return tagService.addRegularTag(learningObjectId, newTag, getLoggedInUser());
    }

    @PutMapping
    @RequestMapping("{learningObjectId}/system_tags")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TagDTO addSystemTag(@PathVariable("learningObjectId") Long learningObjectId, Tag newTag) {
        return tagService.addSystemTag(learningObjectId, newTag, getLoggedInUser());
    }

    @GetMapping
    @RequestMapping("favorite")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR, RoleString.RESTRICTED})
    public UserFavorite hasSetFavorite(@RequestParam("id") Long id) {
        return userFavoriteService.hasFavorited(id, getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("favorite")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public UserFavorite favoriteLearningObject(LearningObject learningObject) {
        return userFavoriteService.addUserFavorite(learningObject, getLoggedInUser());
    }

    @DELETE
    @RequestMapping("favorite")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void removeUserFavorite(@RequestParam("id") long id) {
        userFavoriteService.removeUserFavorite(id, getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("favorite/delete")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void removeUserFavorite2(LearningObjectMiniDto loDto) {
        userFavoriteService.removeUserFavorite(loDto.getId(), getLoggedInUser());
    }

    @GetMapping
    @RequestMapping("usersFavorite")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR, RoleString.RESTRICTED})
    public SearchResult getUsersFavorites(@RequestParam("start") int start, @RequestParam("maxResults") int maxResults) {
        SearchResult result = userFavoriteService.getUserFavoritesSearchResult(getLoggedInUser(), 0, 2147483647);
        result.getItems().sort(Comparator.comparing(Searchable::getType).reversed());
        int stop = min(start + NumberUtils.zvl(maxResults, 12), result.getItems().size());
        result.setItems(result.getItems().subList(start, stop));
        result.setStart(start);
        return result;
    }

    @GetMapping
    @RequestMapping("usersFavorite/count")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR, RoleString.RESTRICTED})
    public Long getUsersFavoritesCount() {
        return userFavoriteService.getUserFavoritesSize(getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("getUserLike")
    public UserLike getUserLike(LearningObjectMiniDto learningObjectMiniDto) {
        return userLikeService.getUserLike(learningObjectMiniDto.convert(), getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("removeUserLike")
    public void removeUserLike(LearningObjectMiniDto learningObjectMiniDto) {
        userLikeService.removeUserLike(learningObjectMiniDto.convert(), getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("like")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void likeMaterial(LearningObjectMiniDto learningObjectMiniDto) {
        userLikeService.addUserLike(learningObjectMiniDto.convert(), getLoggedInUser(), Like.LIKE);
    }

    @PostMapping
    @RequestMapping("dislike")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public void dislikeMaterial(LearningObjectMiniDto learningObjectMiniDto) {
        userLikeService.addUserLike(learningObjectMiniDto.convert(), getLoggedInUser(), Like.DISLIKE);
    }

    @PostMapping
    @RequestMapping("increaseViewCount")
    public void increaseViewCount(LearningObjectMiniDto learningObjectMiniDto) {
        learningObjectService.incrementViewCount(learningObjectMiniDto.convert());
    }
}
