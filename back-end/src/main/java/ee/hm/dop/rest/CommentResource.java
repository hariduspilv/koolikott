package ee.hm.dop.rest;

import ee.hm.dop.model.Comment;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.MaterialService;
import ee.hm.dop.service.content.PortfolioService;
import ee.hm.dop.service.useractions.CommentService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("comment")
@RolesAllowed({ RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR })
public class CommentResource extends BaseResource {

    @Inject
    private CommentService commentService;

    @POST
    @Path("portfolio")
    @Consumes("application/json")
    public void addPortfolioComment(AddCommentForm form) {
        Comment comment = form.getComment();
        User loggedInUser = getLoggedInUser();
        comment.setCreator(loggedInUser);

        commentService.addComment(comment, form.getPortfolio(), loggedInUser);
    }

    @POST
    @Path("material")
    @Consumes("application/json")
    public void addMaterialComment(AddCommentForm form) {
        Comment comment = form.getComment();
        User loggedInUser = getLoggedInUser();
        comment.setCreator(loggedInUser);

        commentService.addComment(comment, form.getMaterial(), loggedInUser);
    }

    public static class AddCommentForm {
        private Comment comment;
        private Portfolio portfolio;
        private Material material;

        public Comment getComment() {
            return comment;
        }

        public void setComment(Comment comment) {
            this.comment = comment;
        }

        public Portfolio getPortfolio() {
            return portfolio;
        }

        public void setPortfolio(Portfolio portfolio) {
            this.portfolio = portfolio;
        }

        public Material getMaterial() {
            return material;
        }

        public void setMaterial(Material material) {
            this.material = material;
        }
    }

}
