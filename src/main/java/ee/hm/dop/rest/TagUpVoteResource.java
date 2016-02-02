package ee.hm.dop.rest;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.service.TagUpVoteService;

@Path("tagUpVotes")
@RolesAllowed({ "USER", "ADMIN", "PUBLISHER", "RESTRICTED" })
public class TagUpVoteResource extends BaseResource {

    @Inject
    private TagUpVoteService tagUpVoteService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TagUpVote upVote(TagUpVote tagUpVote) {
        return tagUpVoteService.upVote(tagUpVote, getLoggedInUser());
    }

    @DELETE
    @Path("tags/{tagID}/materials/{materialID}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void removeVoteMaterial(@PathParam("tagID") Long tagID, @PathParam("materialID") Long materialID) {
        tagUpVoteService.removeUpVoteFromMaterial(tagID, materialID, getLoggedInUser());
    }

    @DELETE
    @Path("tags/{tagID}/portfolios/{portfolioID}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void removeVotePortfolio(@PathParam("tagID") Long tagID, @PathParam("portfolioID") Long portfolioID) {
        tagUpVoteService.removeUpVoteFromPortfolio(tagID, portfolioID, getLoggedInUser());
    }
}
