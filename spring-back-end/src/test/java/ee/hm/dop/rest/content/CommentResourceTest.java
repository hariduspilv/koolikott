package ee.hm.dop.rest.content;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.rest.CommentResource.AddComment;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommentResourceTest extends ResourceIntegrationTestBase {

    public static final String POST_COMMENT_PORTFOLIO_URL = "comment";
    public static final String POST_COMMENT_MATERIAL_URL = "comment";
    public static final String NICE_COMMENT = "This is my comment. Very nice one! :)";
    public static final String SUCH_COMMENT = "Such comment.";

    @Test
    public void addPortfolioComment() {
        login(USER_MATI);
        Portfolio response = doPost(POST_COMMENT_PORTFOLIO_URL, commentForm(NICE_COMMENT, portfolioWithId(PORTFOLIO_5)), Portfolio.class);
        assertTrue(response.getComments().get(0).getText().equals(NICE_COMMENT));
    }

    @Test
    public void addPortfolioCommentNotLoggedIn() {
        Response response = doPost(POST_COMMENT_PORTFOLIO_URL, commentForm(NICE_COMMENT, portfolioWithId(PORTFOLIO_5)));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void addPortfolioCommentToPrivatePortfolioAsCreator() {
        login(USER_PEETER);
        Portfolio response =  doPost(POST_COMMENT_PORTFOLIO_URL, commentForm(SUCH_COMMENT, portfolioWithId(PORTFOLIO_7)), Portfolio.class);
        assertTrue(response.getComments().get(0).getText().equals(SUCH_COMMENT));
    }

    @Test
    public void addPortfolioCommentToPrivatePortfolioAsNotCreator() {
        login(USER_MATI);
        Response response = doPost(POST_COMMENT_PORTFOLIO_URL, commentForm(SUCH_COMMENT, portfolioWithId(PORTFOLIO_7)));
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void addMaterialComment_adds_comment_to_material() throws Exception {
        login(USER_MATI);

        Material materialBefore = getMaterial(MATERIAL_2);
        assertTrue("Material comments empty", materialBefore.getComments().isEmpty());

        Material materialAfter = doPost(POST_COMMENT_MATERIAL_URL, commentForm(NICE_COMMENT, materialWithId(MATERIAL_2)), Material.class);

        assertEquals("Material comments size", 1, materialAfter.getComments().size());
        assertEquals("Material comment", NICE_COMMENT, materialAfter.getComments().get(0).getText());
    }

    @Test
    public void addMaterialComment_as_not_logged_in_user_does_not_add_comment_to_material() throws Exception {
        Response response = doPost(POST_COMMENT_MATERIAL_URL, commentForm(NICE_COMMENT, materialWithId(MATERIAL_3)));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        Material material = getMaterial(MATERIAL_3);
        assertTrue("Material comments empty", material.getComments().isEmpty());
    }

    private Comment makeComment(String text) {
        Comment comment = new Comment();
        comment.setText(text);
        return comment;
    }

    private AddComment commentForm(String niceComment, LearningObject learningObject) {
        AddComment addCommentForm = new AddComment();
        addCommentForm.setLearningObject(learningObject);
        addCommentForm.setComment(makeComment(niceComment));
        return addCommentForm;
    }
}
