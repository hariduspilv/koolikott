package ee.hm.dop.rest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.service.TagUpVoteService;

@Path("tagUpVotes")
public class TagUpVoteResource extends BaseResource {

    @Inject
    private TagUpVoteService tagUpVoteService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "USER", "ADMIN", "PUBLISHER" })
    public TagUpVote upVote(TagUpVote tagUpVote) {
        return tagUpVoteService.upVote(tagUpVote, getLoggedInUser());
    }
}
