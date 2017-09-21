package ee.hm.dop.rest.administration;

import ee.hm.dop.model.ChangedLearningObject;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.ChangedLearningObjectService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("admin/changed/")
public class ChangedLearningObjectAdminResource extends BaseResource {

    @Inject
    private ChangedLearningObjectService changedLearningObjectService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<ChangedLearningObject> getChanged() {
        return changedLearningObjectService.findAll();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<ChangedLearningObject> getChange(@PathParam("id") long id) {
        return changedLearningObjectService.getAllByLearningObject(id);
    }

    @GET
    @Path("{id}/acceptAll")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Response acceptAllChanges(@PathParam("id") long id) {
        changedLearningObjectService.acceptAllChanges(id);
        return ok();
    }

    @GET
    @Path("{id}/revertAll")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject revertAllChanges(@PathParam("id") long id) {
        return changedLearningObjectService.revertAllChanges(id, getLoggedInUser());
    }

    @GET
    @Path("count")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Response getCount() {
        return ok(changedLearningObjectService.getCount());
    }

}
