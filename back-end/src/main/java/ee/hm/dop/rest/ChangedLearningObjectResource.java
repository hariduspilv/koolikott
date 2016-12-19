package ee.hm.dop.rest;

import ee.hm.dop.model.ChangedLearningObject;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.service.ChangedLearningObjectService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("changed")
public class ChangedLearningObjectResource extends BaseResource {

    @Inject
    private ChangedLearningObjectService changedLearningObjectService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public List<ChangedLearningObject> getChanged() {
        return changedLearningObjectService.findAll();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public List<ChangedLearningObject> getChange(@PathParam("id") long id) {
        return changedLearningObjectService.getAllByLearningObject(id);
    }

    @GET
    @Path("{id}/acceptAll")
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public Response acceptAllChanges(@PathParam("id") long id) {
        changedLearningObjectService.acceptAllChanges(id);
        return Response.ok().build();
    }

    @GET
    @Path("{id}/revertAll")
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public LearningObject revertAllChanges(@PathParam("id") long id) {
        return changedLearningObjectService.revertAllChanges(id, getLoggedInUser());
    }

    @GET
    @Path("count")
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public Response getCount() {
        return Response.ok(changedLearningObjectService.getCount()).build();
    }

}
