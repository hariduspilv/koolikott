package ee.hm.dop.rest.administration;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.ReviewType;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.rest.BaseResource;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.service.reviewmanagement.ReviewManager;
import ee.hm.dop.service.reviewmanagement.ReviewableChangeAdminService;
import ee.hm.dop.service.reviewmanagement.ReviewableChangeService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("admin/changed/")
public class ReviewableChangeAdminResource extends BaseResource {

    @Inject
    private ReviewableChangeService reviewableChangeService;
    @Inject
    private ReviewableChangeAdminService reviewableChangeAdminService;
    @Inject
    private ReviewManager reviewManager;
    @Inject
    private LearningObjectService learningObjectService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<AdminLearningObject> getChanged() {
        return reviewableChangeAdminService.getUnReviewed(getLoggedInUser());
    }

    @GET
    @Path("count")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public Long getCount() {
        return reviewableChangeAdminService.getUnReviewedCount(getLoggedInUser());
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<ReviewableChange> getChanges(@PathParam("id") Long learningObjectId) {
        return reviewableChangeService.getAllByLearningObjectOld(learningObjectId);
    }

    @POST
    @Path("byLearningObject")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public List<ReviewableChange> getChangesByLearningObject(LearningObjectMiniDto learningObject) {
        return reviewableChangeService.getAllByLearningObject(learningObject.convert());
    }

    @POST
    @Path("{id}/acceptAll")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject acceptAllChanges(@PathParam("id") Long learningObjectId) {
        if (learningObjectId == null) {
            throw badRequest("learningObject query param is required.");
        }
        User loggedInUser = getLoggedInUser();
        LearningObject learningObject = learningObjectService.get(learningObjectId, loggedInUser);
        if (learningObject == null) {
            throw notFound();
        }
        reviewManager.setEverythingReviewedRefreshLO(getLoggedInUser(), learningObject, ReviewStatus.ACCEPTED, ReviewType.CHANGE);
        return learningObjectService.get(learningObjectId, loggedInUser);
    }

    @POST
    @Path("acceptAll")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject acceptAllChanges2(LearningObjectMiniDto learningObject) {
       return reviewManager.setEverythingReviewedRefreshLO(getLoggedInUser(), learningObject.convert(), ReviewStatus.ACCEPTED, ReviewType.CHANGE);
    }

    @POST
    @Path("{id}/revertAll")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject revertAllChanges(@PathParam("id") Long learningObjectId) {
        return reviewableChangeAdminService.revertAllChanges(learningObjectId, getLoggedInUser());
    }

    @POST
    @Path("revertAll")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject revertAllChanges2(LearningObjectMiniDto learningObject) {
        return reviewableChangeAdminService.revertAllChanges(learningObject.getId(), getLoggedInUser());
    }

    @POST
    @Path("{id}/acceptOne/{changeId}")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject acceptAllChanges(@PathParam("id") Long learningObjectId, @PathParam("changeId") Long changeId) {
        return reviewableChangeAdminService.acceptOneChange(learningObjectId, changeId, getLoggedInUser());
    }

    @POST
    @Path("{id}/revertOne/{changeId}")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject revertAllChanges(@PathParam("id") Long learningObjectId, @PathParam("changeId") Long changeId) {
        return reviewableChangeAdminService.revertOneChange(learningObjectId, changeId, getLoggedInUser());
    }

    @POST
    @Path("acceptOne")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject acceptAllChanges2(ChangeForm changeForm) {
        return reviewableChangeAdminService.acceptOneChange(changeForm.getLearningObject().getId(), changeForm.getChange().getId(), getLoggedInUser());
    }

    @POST
    @Path("revertOne")
    @RolesAllowed({RoleString.ADMIN, RoleString.MODERATOR})
    public LearningObject revertAllChanges2(ChangeForm changeForm) {
        return reviewableChangeAdminService.revertOneChange(changeForm.getLearningObject().getId(), changeForm.getChange().getId(), getLoggedInUser());
    }

    public static class ChangeForm {
        private ReviewableChange change;
        private LearningObject learningObject;

        public ReviewableChange getChange() {
            return change;
        }

        public void setChange(ReviewableChange change) {
            this.change = change;
        }

        public LearningObject getLearningObject() {
            return learningObject;
        }

        public void setLearningObject(LearningObject learningObject) {
            this.learningObject = learningObject;
        }
    }
}
