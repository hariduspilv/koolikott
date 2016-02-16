package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.rest.CommentResource.AddCommentForm;

public class CommentResourceTest extends ResourceIntegrationTestBase {

    private static final String POST_COMMENT_PORTFOLIO_URL = "comment/portfolio";

    @Test
    public void addPortfolioComment() {
        login("39011220011");

        AddCommentForm addCommentForm = new AddCommentForm();

        Portfolio portfolio = new Portfolio();
        long portfolioId = 105L;
        portfolio.setId(portfolioId);
        addCommentForm.setPortfolio(portfolio);

        Comment comment = new Comment();
        String commentText = "This is my comment. Very nice one! :)";
        comment.setText(commentText);
        addCommentForm.setComment(comment);

        Response response = doPost(POST_COMMENT_PORTFOLIO_URL,
                Entity.entity(addCommentForm, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void addPortfolioCommentNotLoggedIn() {
        AddCommentForm addCommentForm = new AddCommentForm();

        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        addCommentForm.setPortfolio(portfolio);

        Comment comment = new Comment();
        comment.setText("This is my comment. Very nice one! :)");
        addCommentForm.setComment(comment);

        Response response = doPost(POST_COMMENT_PORTFOLIO_URL,
                Entity.entity(addCommentForm, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void addPortfolioCommentToPrivatePortfolioAsCreator() {
        login("38011550077");

        AddCommentForm addCommentForm = new AddCommentForm();

        Portfolio portfolio = new Portfolio();
        long portfolioId = 107L;
        portfolio.setId(portfolioId);
        addCommentForm.setPortfolio(portfolio);

        Comment comment = new Comment();
        String commentText = "Such comment.";
        comment.setText(commentText);
        addCommentForm.setComment(comment);

        Response response = doPost(POST_COMMENT_PORTFOLIO_URL,
                Entity.entity(addCommentForm, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void addPortfolioCommentToPrivatePortfolioAsNotCreator() {
        login("39011220011");

        AddCommentForm addCommentForm = new AddCommentForm();

        Portfolio portfolio = new Portfolio();
        long portfolioId = 7L;
        portfolio.setId(portfolioId);
        addCommentForm.setPortfolio(portfolio);

        Comment comment = new Comment();
        String commentText = "Such comment.";
        comment.setText(commentText);
        addCommentForm.setComment(comment);

        Response response = doPost(POST_COMMENT_PORTFOLIO_URL,
                Entity.entity(addCommentForm, MediaType.APPLICATION_JSON_TYPE));

        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

}
