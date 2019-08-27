package ee.hm.dop.rest;

import ee.hm.dop.model.Media;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.MediaService;
import ee.hm.dop.service.useractions.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
@RequestMapping("media")
public class MediaResource extends BaseResource {

    @Inject
    private MediaService mediaService;
    @Inject
    private UserService userService;

    @GetMapping
    public Media get(@RequestParam("id") long mediaId) {
        return mediaService.get(mediaId);
    }

    @PostMapping
    @RequestMapping("create")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public Media createMedia(@RequestBody Media media) {
        return mediaService.save(media, getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("update")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public Media updateMedia(@RequestBody Media media) {
        return mediaService.update(media, getLoggedInUser());
    }

    @GetMapping("getAllMediaByCreator")
    public List<Media> getAllByCreator(@RequestParam("username") String username) {
        User creator = getValidCreator(username);
        if (creator == null) throw notFound();
        return mediaService.getAllByCreator(creator);
    }

    private User getValidCreator(@RequestParam("username") String username) {
        if (isBlank(username)) throw badRequest("Username parameter is mandatory");
        return userService.getUserByUsername(username);
    }

}
