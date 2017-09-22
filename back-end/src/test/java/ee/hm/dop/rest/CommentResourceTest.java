package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Comment;
import ee.hm.dop.rest.CommentResource.AddCommentForm;
import org.junit.Test;

public class CommentResourceTest extends ResourceIntegrationTestBase {

    public static final String POST_COMMENT_PORTFOLIO_URL = "comment/portfolio";
    public static final String NICE_COMMENT = "This is my comment. Very nice one! :)";
    public static final String SUCH_COMMENT = "Such comment.";

    @Test
    public void addPortfolioComment() {
        login(USER_MATI);
        Response response = doPost(POST_COMMENT_PORTFOLIO_URL, commentForm(105L, NICE_COMMENT));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void addPortfolioCommentNotLoggedIn() {
        Response response = doPost(POST_COMMENT_PORTFOLIO_URL, commentForm(1L, NICE_COMMENT));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void addPortfolioCommentToPrivatePortfolioAsCreator() {
        login(USER_PEETER);
        Response response = doPost(POST_COMMENT_PORTFOLIO_URL, commentForm(107L, SUCH_COMMENT));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void addPortfolioCommentToPrivatePortfolioAsNotCreator() {
        login(USER_MATI);
        Response response = doPost(POST_COMMENT_PORTFOLIO_URL, commentForm(7L, SUCH_COMMENT));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    private Comment makeComment(String text) {
        Comment comment = new Comment();
        comment.setText(text);
        return comment;
    }

    private AddCommentForm commentForm(long portfolioId, String niceComment) {
        AddCommentForm addCommentForm = new AddCommentForm();
        addCommentForm.setPortfolio(portfolioWithId(portfolioId));
        addCommentForm.setComment(makeComment(niceComment));
        return addCommentForm;
    }
}
