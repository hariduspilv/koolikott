package ee.hm.dop.rest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import ee.hm.dop.model.Comment;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
import ee.hm.dop.rest.filter.DopPrincipal;
import ee.hm.dop.service.PortfolioService;

@Path("comment")
@RolesAllowed("USER")
public class CommentResource {

    @Inject
    private PortfolioService portfolioService;

    @Context
    private HttpServletRequest request;

    private SecurityContext securityContext;

    @Context
    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    @POST
    @Path("portfolio")
    @Consumes("application/json")
    public void addPortfolioComment(AddCommentForm form) {

        User user = ((DopPrincipal) securityContext.getUserPrincipal()).getAuthenticatedUser().getUser();
        Comment comment = form.getComment();
        comment.setCreator(user);

        portfolioService.addComment(comment, form.getPortfolio());
    }

    public static class AddCommentForm {

        private Comment comment;
        private Portfolio portfolio;

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
    }

}
