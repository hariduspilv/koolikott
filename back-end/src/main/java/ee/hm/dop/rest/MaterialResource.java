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

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Path("material")
public class MaterialResource extends BaseResource {

    @Inject
    private MaterialService materialService;
    @Inject
    private UserService userService;
    @Inject
    private LearningObjectAdministrationService learningObjectAdministrationService;
    @Inject
    private MaterialGetter materialGetter;



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Material get(@QueryParam("id") long materialId) {
        return materialGetter.get(materialId, getLoggedInUser());
    }

    @GET
    @Path("chapter")
    @Produces(MediaType.APPLICATION_JSON)
    public Material getWithoutValidation(@QueryParam("id") long materialId) {
        return materialGetter.getWithoutValidation(materialId);
    }

    @GET
    @Path("getByCreator")
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult getByCreator(@QueryParam("username") String username, @QueryParam("start") int start, @QueryParam("maxResults") int maxResults) {
        User creator = getValidCreator(username);
        return (creator != null) ? materialGetter.getByCreatorResult(creator, start, NumberUtils.zvl(maxResults, 12)) : null;
    }

    @GET
    @Path("getByCreator/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Long getByCreatorCount(@QueryParam("username") String username) {
        User creator = getValidCreator(username);
        return (creator != null) ? materialGetter.getByCreatorSize(creator) : null;
    }

    @GET
    @Path("getBySource")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Material> getMaterialsByUrl(@QueryParam("source") @Encoded String materialSource) throws UnsupportedEncodingException {
        return materialGetter.getBySource(decode(materialSource), GetMaterialStrategy.ONLY_EXISTING);
    }

    @GET
    @Path("getOneBySource")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Produces(MediaType.APPLICATION_JSON)
    public Material getMaterialByUrl(@QueryParam("source") @Encoded String materialSource) throws UnsupportedEncodingException {
        return materialGetter.getAnyBySource(decode(materialSource), GetMaterialStrategy.INCLUDE_DELETED);
    }

    private User getValidCreator(@QueryParam("username") String username) {
        if (isBlank(username)) throw badRequest("Username parameter is mandatory");
        return userService.getUserByUsername(username);
    }

    @POST
    @Path("create")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Material createMaterial(Material material) {
        if (material.getId() == null) {
            return materialService.createMaterial(material, getLoggedInUser(), SearchIndexStrategy.UPDATE_INDEX);
        }
        throw new UnsupportedOperationException("this is create method");
    }

    @POST
    @Path("update")
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Material updateMaterial(Material material) {
        if (material.getId() == null) {
            throw new UnsupportedOperationException("this is update method");
        }
        return materialService.update(material, getLoggedInUser(), SearchIndexStrategy.UPDATE_INDEX);
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject delete(Material material) {
        return learningObjectAdministrationService.delete(material, getLoggedInUser());
    }

    @GET
    @Path("getRelatedPortfolios")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Portfolio> getRelatedPortfolios(@QueryParam("id") Long id) {
        return materialService.getRelatedPortfolios(id);
    }

    @GET
    @Path("showUnreviewedMaterial")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean showUnreviewedMaterial(@QueryParam("materialId") Long id) {
        return materialService.showUnreviewedMaterial(id, getLoggedInUser());
    }
}
