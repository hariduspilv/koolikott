package ee.hm.dop.rest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import ee.hm.dop.model.Comment;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.service.MaterialService;
import ee.hm.dop.service.PortfolioService;

@Path("comment")
@RolesAllowed("USER")
public class CommentResource extends BaseResource {

    @Inject
    private PortfolioService portfolioService;

    @Inject
    private MaterialService materialService;
    
    @POST
    @Path("portfolio")
    @Consumes("application/json")
    public void addPortfolioComment(AddCommentForm form) {

        Comment comment = form.getComment();
        comment.setCreator(getLoggedInUser());

        portfolioService.addComment(comment, form.getPortfolio());
    }
    
    @POST
    @Path("material")
    @Consumes("application/json")
    public void addMaterialComment(AddCommentForm form) {

        Comment comment = form.getComment();
        comment.setCreator(getLoggedInUser());

        materialService.addComment(comment, form.getMaterial());
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
