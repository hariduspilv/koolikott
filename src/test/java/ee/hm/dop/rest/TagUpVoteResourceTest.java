package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.model.TagUpVoteForm;

public class TagUpVoteResourceTest extends ResourceIntegrationTestBase {

    public static final String TAG_UP_VOTES_MATERIAL_1_TAG_MATEMAATIKA = "tagUpVotes?material=1&tag=matemaatika";
    public static final String TAG_UP_VOTES = "tagUpVotes";

    @Test
    public void upVote() {
        login("89012378912");

        Material material = new Material();
        material.setId(1l);

        Tag tag = new Tag();
        tag.setName("matemaatika");

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag);
        tagUpVote.setMaterial(material);

        Response response = doGet("tagUpVotes?material=1");
        List<TagUpVoteForm> tagUpVoteForms = response.readEntity(new GenericType<List<TagUpVoteForm>>() {
        });
        int size = tagUpVoteForms.size();

        response = doPut(TAG_UP_VOTES, Entity.entity(tagUpVote, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        TagUpVote returnedTagUpVote = response.readEntity(TagUpVote.class);

        assertNotNull(returnedTagUpVote);
        assertNotNull(returnedTagUpVote.getId());

        response = doDelete(TAG_UP_VOTES_MATERIAL_1_TAG_MATEMAATIKA);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = doGet("tagUpVotes?material=1");
        tagUpVoteForms = response.readEntity(new GenericType<List<TagUpVoteForm>>() {
        });
        assertEquals(size, tagUpVoteForms.size());

        logout();
    }

    @Test
    public void getTagUpVotesMaterial() {
        Response response = doGet("tagUpVotes?material=1");
        List<TagUpVoteForm> tagUpVoteForms = response.readEntity(new GenericType<List<TagUpVoteForm>>() {
        });

        assertEquals(5, tagUpVoteForms.size());
        assertEquals(1, tagUpVoteForms.get(0).getUpVoteCount());
        assertEquals(0, tagUpVoteForms.get(1).getUpVoteCount());
    }

    @Test
    public void getTagUpVotesNoMaterial() {
        Response response = doGet("tagUpVotes?material=99");
        List<TagUpVoteForm> tagUpVoteForms = response.readEntity(new GenericType<List<TagUpVoteForm>>() {
        });

        assertEquals(0, tagUpVoteForms.size());
    }

    @Test
    public void getTagUpVotesNoTags() {
        Response response = doGet("tagUpVotes?material=3");
        List<TagUpVoteForm> tagUpVoteForms = response.readEntity(new GenericType<List<TagUpVoteForm>>() {
        });

        assertEquals(0, tagUpVoteForms.size());
    }

    @Test
    public void getTagUpVotesPortfolio() {
        Response response = doGet("tagUpVotes?portfolio=101");
        List<TagUpVoteForm> tagUpVoteForms = response.readEntity(new GenericType<List<TagUpVoteForm>>() {
        });

        assertEquals(5, tagUpVoteForms.size());
        assertEquals(1, tagUpVoteForms.get(0).getUpVoteCount());
        assertEquals(0, tagUpVoteForms.get(1).getUpVoteCount());

    }

    @Test
    public void removeUpVoteMaterial() {
        login("89012378912");

        Material material = new Material();
        material.setId(1l);

        Tag tag = new Tag();
        tag.setName("põhikool");

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag);
        tagUpVote.setMaterial(material);

        Response response = doGet("tagUpVotes?material=1");
        List<TagUpVoteForm> tagUpVoteForms = response.readEntity(new GenericType<List<TagUpVoteForm>>() {
        });
        int size = tagUpVoteForms.size();

        response = doPut(TAG_UP_VOTES, Entity.entity(tagUpVote, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        TagUpVote returnedTagUpVote = response.readEntity(TagUpVote.class);

        assertNotNull(returnedTagUpVote);
        assertNotNull(returnedTagUpVote.getId());

        response = doDelete("tagUpVotes?material=1&tag=põhikool");
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = doGet("tagUpVotes?material=1");
        tagUpVoteForms = response.readEntity(new GenericType<List<TagUpVoteForm>>() {
        });

        assertEquals(size, tagUpVoteForms.size());

        logout();
    }

    @Test
    public void removeUpVotePortfolio() {
        login("89012378912");

        Portfolio portfolio = new Portfolio();
        portfolio.setId(101l);

        Tag tag = new Tag();
        tag.setName("matemaatika");

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag);
        tagUpVote.setPortfolio(portfolio);

        Response response = doGet("tagUpVotes?portfolio=101");
        List<TagUpVoteForm> tagUpVoteForms = response.readEntity(new GenericType<List<TagUpVoteForm>>() {
        });
        int size = tagUpVoteForms.size();

        response = doPut(TAG_UP_VOTES, Entity.entity(tagUpVote, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        TagUpVote returnedTagUpVote = response.readEntity(TagUpVote.class);

        assertNotNull(returnedTagUpVote);
        assertNotNull(returnedTagUpVote.getId());

        response = doDelete("tagUpVotes?portfolio=101&tag=matemaatika");
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        response = doGet("tagUpVotes?portfolio=101");
        tagUpVoteForms = response.readEntity(new GenericType<List<TagUpVoteForm>>() {
        });

        assertEquals(size, tagUpVoteForms.size());

        logout();
    }

    @Test
    public void upVoteGettingPrivatePortfolio() {
        login("89012378912"); // Regular user

        Portfolio portfolio = new Portfolio();
        portfolio.setId(10L);

        Tag tag = new Tag();
        tag.setName("matemaatika");

        TagUpVote tagUpVote = new TagUpVote();
        tagUpVote.setTag(tag);
        tagUpVote.setPortfolio(portfolio);

        Response response = doPut(TAG_UP_VOTES, Entity.entity(tagUpVote, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        logout();
    }
}
