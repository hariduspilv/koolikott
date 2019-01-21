package ee.hm.dop.rest;

import ee.hm.dop.model.Media;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.MediaService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("media")
public class MediaResource extends BaseResource {

    @Inject
    private MediaService mediaService;

    @GetMapping

    public Media get(@RequestParam("id") long mediaId) {
        return mediaService.get(mediaId);
    }

    @PostMapping
    @RequestMapping("create")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)

    public Media createMedia(@RequestBody Media media) {
        return mediaService.save(media, getLoggedInUser());
    }

    @PostMapping
    @RequestMapping("update")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)

    public Media updateMedia(@RequestBody Media media) {
        return mediaService.update(media, getLoggedInUser());
    }

}
