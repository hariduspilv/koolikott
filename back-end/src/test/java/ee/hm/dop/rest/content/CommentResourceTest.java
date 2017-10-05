package ee.hm.dop.rest.content;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.common.test.TestConstants;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.Material;
import ee.hm.dop.rest.CommentResource.AddCommentForm;
import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommentResourceTest extends ResourceIntegrationTestBase {

    public static final String POST_COMMENT_PORTFOLIO_URL = "comment/portfolio";
    public static final String POST_COMMENT_MATERIAL_URL = "comment/material";
    public static final String NICE_COMMENT = "This is my comment. Very nice one! :)";
    public static final String SUCH_COMMENT = "Such comment.";

    @Test
    public void addPortfolioComment() {
        login(USER_MATI);
        Response response = doPost(POST_COMMENT_PORTFOLIO_URL, commentForm(PORTFOLIO_5, NICE_COMMENT));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void addPortfolioCommentNotLoggedIn() {
        Response response = doPost(POST_COMMENT_PORTFOLIO_URL, commentForm(PORTFOLIO_5, NICE_COMMENT));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void addPortfolioCommentToPrivatePortfolioAsCreator() {
        login(USER_PEETER);
        Response response = doPost(POST_COMMENT_PORTFOLIO_URL, commentForm(PORTFOLIO_7, SUCH_COMMENT));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void addPortfolioCommentToPrivatePortfolioAsNotCreator() {
        login(USER_MATI);
        Response response = doPost(POST_COMMENT_PORTFOLIO_URL, commentForm(PORTFOLIO_7, SUCH_COMMENT));
        assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void addMaterialComment_adds_comment_to_material() throws Exception {
        login(USER_MATI);

        Material materialBefore = getMaterial(MATERIAL_2);
        assertTrue("Material comments empty", materialBefore.getComments().isEmpty());

        doPost(POST_COMMENT_MATERIAL_URL, commentMaterialForm(MATERIAL_2, NICE_COMMENT));

        Material materialAfter = getMaterial(MATERIAL_2);
        assertEquals("Material comments size", 1, materialAfter.getComments().size());
        assertEquals("Material comment", NICE_COMMENT, materialAfter.getComments().get(0).getText());
    }

    @Test
    public void addMaterialComment_as_not_logged_in_user_does_not_add_comment_to_material() throws Exception {
        Response response = doPost(POST_COMMENT_MATERIAL_URL, commentMaterialForm(MATERIAL_3, NICE_COMMENT));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        Material material = getMaterial(MATERIAL_3);
        assertTrue("Material comments empty", material.getComments().isEmpty());
    }

    private Comment makeComment(String text) {
        Comment comment = new Comment();
        comment.setText(text);
        return comment;
    }

    private AddCommentForm commentForm(Long portfolioId, String niceComment) {
        AddCommentForm addCommentForm = new AddCommentForm();
        addCommentForm.setPortfolio(portfolioWithId(portfolioId));
        addCommentForm.setComment(makeComment(niceComment));
        return addCommentForm;
    }

    private AddCommentForm commentMaterialForm(Long materialId, String comment) {
        AddCommentForm addCommentForm = new AddCommentForm();
        addCommentForm.setMaterial(materialWithId(materialId));
        addCommentForm.setComment(makeComment(comment));
        return addCommentForm;
    }
}
