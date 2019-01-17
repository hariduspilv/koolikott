package ee.hm.dop.rest;

import ee.hm.dop.model.Comment;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.useractions.CommentService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("comment")
@RolesAllowed({ RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR })
public class CommentResource extends BaseResource {

    @Inject
    private CommentService commentService;

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public LearningObject addComment(AddComment form) {
        return commentService.addComment(form.getComment(), form.getLearningObject(), getLoggedInUser());
    }

    public static class AddComment {
        private Comment comment;
        private LearningObject learningObject;

        public Comment getComment() {
            return comment;
        }

        public void setComment(Comment comment) {
            this.comment = comment;
        }

        public LearningObject getLearningObject() {
            return learningObject;
        }

        public void setLearningObject(LearningObject learningObject) {
            this.learningObject = learningObject;
        }
    }

}
