package ee.hm.dop.rest;

import ee.hm.dop.model.*;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
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
    public Material get(@RequestParam("id") long materialId) {
        return materialGetter.get(materialId, getLoggedInUser());
    }

    @GetMapping("chapter")
    public Material getWithoutValidation(@RequestParam("id") long materialId) {
        return materialGetter.getWithoutValidation(materialId);
    }

    @GetMapping("getByCreator")
    public SearchResult getByCreator(@RequestParam("username") String username,
                                     @RequestParam(value = "start", defaultValue = "0") int start,
                                     @RequestParam(value = "maxResults", defaultValue = "0") int maxResults) {
        User creator = getValidCreator(username);
        return (creator != null) ? materialGetter.getByCreatorResult(creator, start, NumberUtils.zvl(maxResults, 12)) : null;
    }

    @GetMapping("getByCreator/count")
    public Long getByCreatorCount(@RequestParam("username") String username) {
        User creator = getValidCreator(username);
        return (creator != null) ? materialGetter.getByCreatorSize(creator) : null;
    }

    @GetMapping("getBySource")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public List<Material> getMaterialsByUrl(@RequestParam("source") @Encoded String materialSource) throws UnsupportedEncodingException {
        return materialGetter.getBySource(decode(materialSource), GetMaterialStrategy.ONLY_EXISTING);
    }

    @GetMapping("getOneBySource")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public Material getMaterialByUrl(@RequestParam("source") @Encoded String materialSource) throws UnsupportedEncodingException {
        return materialGetter.getAnyBySource(decode(materialSource), GetMaterialStrategy.INCLUDE_DELETED);
    }

    private User getValidCreator(@RequestParam("username") String username) {
        if (isBlank(username)) throw badRequest("Username parameter is mandatory");
        return userService.getUserByUsername(username);
    }

    @PostMapping("create")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    public Material createMaterial(@RequestBody Material material) {
        if (material.getId() == null) {
            return materialService.createMaterial(material, getLoggedInUser(), SearchIndexStrategy.UPDATE_INDEX);
        }
        throw new UnsupportedOperationException("this is create method");
    }

    @PostMapping("update")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    public Material updateMaterial(@RequestBody Material material) {
        if (material.getId() == null) {
            throw new UnsupportedOperationException("this is update method");
        }
        return materialService.update(material, getLoggedInUser(), SearchIndexStrategy.UPDATE_INDEX);
    }

    @PostMapping("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject delete(@RequestBody Material material) {
        return learningObjectAdministrationService.delete(material, getLoggedInUser());
    }

    @GET
    @Path("getRelatedPortfolios")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Portfolio> getRelatedPortfolios(@QueryParam("id") Long id) {
        return materialService.getRelatedPortfolios(id);
    }
}
