package ee.hm.dop.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TagUpVote;

public class TagUpVoteResourceTest extends ResourceIntegrationTestBase {


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

        Response response = doPut("tagUpVotes", Entity.entity(tagUpVote, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        TagUpVote returnedTagUpVote = response.readEntity(TagUpVote.class);

        assertNotNull(returnedTagUpVote);
        assertNotNull(returnedTagUpVote.getId());
    }
}
