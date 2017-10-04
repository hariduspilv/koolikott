package ee.hm.dop.rest.administration;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.ImproperContentService;
import ee.hm.dop.service.content.LearningObjectAdministrationService;
import ee.hm.dop.service.content.LearningObjectService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("impropers")
public class ImproperContentAdminResource extends BaseResource {

    @Inject
    private LearningObjectAdministrationService learningObjectAdministrationService;
    @Inject
    private LearningObjectService learningObjectService;

    @DELETE
    @RolesAllowed({RoleString.MODERATOR, RoleString.ADMIN})
    public void removeImpropers(@QueryParam("learningObject") Long learningObjectId) {
        if (learningObjectId == null) {
            throw badRequest("learningObject query param is required.");
        }
        User loggedInUser = getLoggedInUser();
        LearningObject learningObject = learningObjectService.get(learningObjectId, loggedInUser);
        if (learningObject == null) {
            throw notFound();
        }
        learningObjectAdministrationService.setEverythingReviewedRefreshLO(loggedInUser, learningObject, ReviewStatus.ACCEPTED);
    }
}
