package ee.hm.dop.rest;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.rest.TagUpVoteResource.TagUpVoteForm;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.*;

public class TagUpVoteResourceTest extends ResourceIntegrationTestBase {

    public static final String TAG_UP_VOTES = "tagUpVotes";
    public static final String MATEMAATIKA = "matemaatika";
    public static final String NOT_EXISTING_TAG = "keemia";

    @Test
    public void upVote() {
        login(USER_SECOND);

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag(MATEMAATIKA));
        tagUpVote.setLearningObject(materialWithId(1L));

        TagUpVote returnedTagUpVote = doPut(TAG_UP_VOTES, tagUpVote, TagUpVote.class);

        assertNotNull(returnedTagUpVote);
        assertNotNull(returnedTagUpVote.getId());
        assertNull(returnedTagUpVote.getUser().getIdCode());
        assertEquals(MATEMAATIKA, returnedTagUpVote.getTag().getName());
        assertEquals((Long) 1L, returnedTagUpVote.getLearningObject().getId());

        Response response = doDelete(TAG_UP_VOTES + "/" + returnedTagUpVote.getId());
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void reportNotLoggedIn() {
        List<TagUpVoteForm> tagUpVoteForms = doGet("tagUpVotes/report?learningObject=1", list());

        assertEquals(5, tagUpVoteForms.size());

        for (TagUpVoteForm form : tagUpVoteForms) {
            assertNotNull(form.getTag());
            assertNull(form.getTagUpVote());

            if (form.getTag().getId() == 1) {
                assertEquals(1, form.getUpVoteCount());
            } else {
                assertEquals(0, form.getUpVoteCount());
            }
        }
    }

    @Test
    public void report() {
        login(USER_MATI);

        Response response = doGet("tagUpVotes/report?learningObject=1");
        List<TagUpVoteForm> tagUpVoteForms = response.readEntity(list());

        assertEquals(5, tagUpVoteForms.size());

        for (TagUpVoteForm form : tagUpVoteForms) {
            assertNotNull(form.getTag());

            if (form.getTag().getId() == 1) {
                assertEquals(1, form.getUpVoteCount());
                assertNotNull(form.getTagUpVote());
                assertEquals(new Long(2), form.getTagUpVote().getId());
            } else {
                assertEquals(0, form.getUpVoteCount());
                assertNull(form.getTagUpVote());
            }
        }
    }

    @Test
    public void reportNoLearningObject() {
        Response response = doGet("tagUpVotes/report?learningObject=99");
        List<TagUpVoteForm> tagUpVoteForms = response.readEntity(list());

        assertEquals(0, tagUpVoteForms.size());
    }

    @Test
    public void getTagUpVotesNoTags() {
        Response response = doGet("tagUpVotes/report?learningObject=3");
        List<TagUpVoteForm> tagUpVoteForms = response.readEntity(list());

        assertEquals(0, tagUpVoteForms.size());
    }

    @Test
    public void removeUpVote() {
        login(USER_SECOND);

        Portfolio portfolio = new Portfolio();
        portfolio.setId(101L);
        portfolio.setVisibility(Visibility.PUBLIC);

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag(MATEMAATIKA));
        tagUpVote.setLearningObject(portfolio);

        TagUpVote returnedTagUpVote = doPut(TAG_UP_VOTES, tagUpVote, TagUpVote.class);

        assertNotNull(returnedTagUpVote);
        assertNotNull(returnedTagUpVote.getId());

        Response response = doDelete(TAG_UP_VOTES + "/" + returnedTagUpVote.getId());
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void can_not_remove_tagUpVote_that_does_not_exist() throws Exception {
        login(USER_SECOND);
        Response response = doDelete(TAG_UP_VOTES + "/" + 100L);
        assertEquals("No tagUpVote", Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void can_not_get_tagUpVote_without_learningObject_id() throws Exception {
        login(USER_SECOND);
        Response response = doGet("tagUpVotes/report?learningObject=");
        assertEquals("LearningObject query param is required", Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void can_not_insert_tagUpVote_to_tag_that_does_not_exist() throws Exception {
        login(USER_SECOND);

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag(NOT_EXISTING_TAG));
        tagUpVote.setLearningObject(portfolioWithId(101L));

        Response response = doPut(TAG_UP_VOTES, tagUpVote);
        assertEquals("No tag", Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void can_not_insert_tagUpVote_that_already_exists() throws Exception {
        login(USER_SECOND);

        Portfolio portfolio = new Portfolio();
        portfolio.setId(101L);
        portfolio.setVisibility(Visibility.PUBLIC);

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag(MATEMAATIKA));
        tagUpVote.setLearningObject(portfolio);

        TagUpVote returnedTagUpVote = doPut(TAG_UP_VOTES, tagUpVote, TagUpVote.class);
        Response response = doPut(TAG_UP_VOTES, returnedTagUpVote);
        assertEquals("TagUpVote already exists", Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        doDelete(TAG_UP_VOTES + "/" + returnedTagUpVote.getId());
    }

    @Test
    public void upVoteGettingPrivatePortfolio() {
        login(USER_SECOND);

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag(MATEMAATIKA));
        tagUpVote.setLearningObject(portfolioWithId(110L));

        Response response = doPut(TAG_UP_VOTES, tagUpVote);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    private GenericType<List<TagUpVoteForm>> list() {
        return new GenericType<List<TagUpVoteForm>>() {
        };
    }
}
