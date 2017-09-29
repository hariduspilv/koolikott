package ee.hm.dop.rest.administration;

import com.google.common.collect.Lists;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.ImproperContentService;
import ee.hm.dop.service.content.LearningObjectService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.List;

@Path("impropers")
public class ImproperContentAdminResource extends BaseResource {

    @Inject
    private ImproperContentService improperContentService;
    @Inject
    private LearningObjectService learningObjectService;

    @DELETE
    @RolesAllowed({RoleString.ADMIN})
    public void removeImpropers(@QueryParam("learningObject") Long learningObjectId) {
        if (learningObjectId == null) {
            throw badRequest("learningObject query param is required.");
        }
        LearningObject learningObject = learningObjectService.get(learningObjectId, getLoggedInUser());
        if (learningObject == null) {
            throw notFound();
        }
        List<ImproperContent> impropers = improperContentService.getByLearningObject(learningObject, getLoggedInUser());
        improperContentService.deleteAll(impropers, getLoggedInUser());
    }

    @DELETE
    @Path("{improperContentId}")
    @RolesAllowed({RoleString.ADMIN})
    public void removeImproper(@PathParam("improperContentId") long improperContentId) {
        ImproperContent improper = improperContentService.get(improperContentId, getLoggedInUser());
        if (improper == null) {
            throw notFound();
        }
        improperContentService.deleteAll(Lists.newArrayList(improper), getLoggedInUser());
    }
}
