package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.model.Visibility;
import ee.hm.dop.rest.TagUpVoteResource.TagUpVoteForm;
import org.junit.Test;

public class TagUpVoteResourceTest extends ResourceIntegrationTestBase {

    public static final String TAG_UP_VOTES = "tagUpVotes";

    @Test
    public void upVote() {
        String idCode = "89012378912";
        login(idCode);

        Material material = new Material();
        material.setId(1l);

        Tag tag = new Tag();
        String tagName = "matemaatika";
        tag.setName(tagName);

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag);
        tagUpVote.setLearningObject(material);

        TagUpVote returnedTagUpVote = doPut(TAG_UP_VOTES, Entity.entity(tagUpVote, MediaType.APPLICATION_JSON_TYPE),
                TagUpVote.class);

        assertNotNull(returnedTagUpVote);
        assertNotNull(returnedTagUpVote.getId());
        assertNull(returnedTagUpVote.getUser().getIdCode());
        assertEquals(tagName, returnedTagUpVote.getTag().getName());
        assertEquals(material.getId(), returnedTagUpVote.getLearningObject().getId());

        Response response = doDelete(TAG_UP_VOTES + "/" + returnedTagUpVote.getId());
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void reportNotLoggedIn() {
        Response response = doGet("tagUpVotes/report?learningObject=1");
        List<TagUpVoteForm> tagUpVoteForms = response.readEntity(new GenericType<List<TagUpVoteForm>>() {
        });

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
        login("39011220011");

        Response response = doGet("tagUpVotes/report?learningObject=1");
        List<TagUpVoteForm> tagUpVoteForms = response.readEntity(new GenericType<List<TagUpVoteForm>>() {
        });

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
        List<TagUpVoteForm> tagUpVoteForms = response.readEntity(new GenericType<List<TagUpVoteForm>>() {
        });

        assertEquals(0, tagUpVoteForms.size());
    }

    @Test
    public void getTagUpVotesNoTags() {
        Response response = doGet("tagUpVotes/report?learningObject=3");
        List<TagUpVoteForm> tagUpVoteForms = response.readEntity(new GenericType<List<TagUpVoteForm>>() {
        });

        assertEquals(0, tagUpVoteForms.size());
    }

    @Test
    public void removeUpVote() {
        login("89012378912");

        Portfolio portfolio = new Portfolio();
        portfolio.setId(101l);
        portfolio.setVisibility(Visibility.PUBLIC);

        Tag tag = new Tag();
        tag.setName("matemaatika");

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag);
        tagUpVote.setLearningObject(portfolio);

        TagUpVote returnedTagUpVote = doPut(TAG_UP_VOTES, Entity.entity(tagUpVote, MediaType.APPLICATION_JSON_TYPE),
                TagUpVote.class);

        assertNotNull(returnedTagUpVote);
        assertNotNull(returnedTagUpVote.getId());

        Response response = doDelete(TAG_UP_VOTES + "/" + returnedTagUpVote.getId());
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void upVoteGettingPrivatePortfolio() {
        login("89012378912"); // Regular user

        Portfolio portfolio = new Portfolio();
        portfolio.setId(110L);

        Tag tag = new Tag();
        tag.setName("matemaatika");

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag);
        tagUpVote.setLearningObject(portfolio);

        Response response = doPut(TAG_UP_VOTES, Entity.entity(tagUpVote, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
}
