package ee.hm.dop.rest;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.LearningObjectAdministrationService;
import ee.hm.dop.service.content.MaterialGetter;
import ee.hm.dop.service.content.MaterialService;
import ee.hm.dop.service.content.enums.GetMaterialStrategy;
import ee.hm.dop.service.content.enums.SearchIndexStrategy;
import ee.hm.dop.service.useractions.UserService;
import ee.hm.dop.utils.NumberUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RequestMapping("material")
public class MaterialResource extends BaseResource {

    @Inject
    private MaterialService materialService;
    @Inject
    private UserService userService;
    @Inject
    private LearningObjectAdministrationService learningObjectAdministrationService;
    @Inject
    private MaterialGetter materialGetter;

    @GetMapping
    @Produces(MediaType.APPLICATION_JSON)
    public Material get(@RequestParam("id") long materialId) {
        return materialGetter.get(materialId, getLoggedInUser());
    }

    @GetMapping
    @RequestMapping("chapter")
    @Produces(MediaType.APPLICATION_JSON)
    public Material getWithoutValidation(@RequestParam("id") long materialId) {
        return materialGetter.getWithoutValidation(materialId);
    }

    @GetMapping
    @RequestMapping("getByCreator")
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult getByCreator(@RequestParam("username") String username, @RequestParam("start") int start, @RequestParam("maxResults") int maxResults) {
        User creator = getValidCreator(username);
        return (creator != null) ? materialGetter.getByCreatorResult(creator, start, NumberUtils.zvl(maxResults, 12)) : null;
    }

    @GetMapping
    @RequestMapping("getByCreator/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Long getByCreatorCount(@RequestParam("username") String username) {
        User creator = getValidCreator(username);
        return (creator != null) ? materialGetter.getByCreatorSize(creator) : null;
    }

    @GetMapping
    @RequestMapping("getBySource")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Material> getMaterialsByUrl(@RequestParam("source") @Encoded String materialSource) throws UnsupportedEncodingException {
        return materialGetter.getBySource(decode(materialSource), GetMaterialStrategy.ONLY_EXISTING);
    }

    @GetMapping
    @RequestMapping("getOneBySource")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Material getMaterialByUrl(@RequestParam("source") @Encoded String materialSource) throws UnsupportedEncodingException {
        return materialGetter.getAnyBySource(decode(materialSource), GetMaterialStrategy.INCLUDE_DELETED);
    }

    private User getValidCreator(@RequestParam("username") String username) {
        if (isBlank(username)) throw badRequest("Username parameter is mandatory");
        return userService.getUserByUsername(username);
    }

    @PostMapping
    @RequestMapping("create")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Material createMaterial(Material material) {
        if (material.getId() == null) {
            return materialService.createMaterial(material, getLoggedInUser(), SearchIndexStrategy.UPDATE_INDEX);
        }
        throw new UnsupportedOperationException("this is create method");
    }

    @PostMapping
    @RequestMapping("update")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Material updateMaterial(Material material) {
        if (material.getId() == null) {
            throw new UnsupportedOperationException("this is update method");
        }
        return materialService.update(material, getLoggedInUser(), SearchIndexStrategy.UPDATE_INDEX);
    }

    @PostMapping
    @RequestMapping("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject delete(Material material) {
        return learningObjectAdministrationService.delete(material, getLoggedInUser());
    }
}
