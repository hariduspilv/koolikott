package ee.hm.dop.rest;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.model.TagUpVoteForm;
import ee.hm.dop.service.MaterialService;
import ee.hm.dop.service.PortfolioService;
import ee.hm.dop.service.TagService;
import ee.hm.dop.service.TagUpVoteService;

@Path("tagUpVotes")
@RolesAllowed({ "USER", "ADMIN", "PUBLISHER", "RESTRICTED" })
public class TagUpVoteResource extends BaseResource {

    @Inject
    private TagUpVoteService tagUpVoteService;

    @Inject
    private MaterialService materialService;

    @Inject
    private PortfolioService portfolioService;

    @Inject
    private TagService tagService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TagUpVote upVote(TagUpVote tagUpVote) {
        return tagUpVoteService.upVote(tagUpVote, getLoggedInUser());
    }

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<TagUpVoteForm> getTagUpVotes(@QueryParam("material") Long materialID, @QueryParam("portfolio") Long portfolioID) {
        List<TagUpVoteForm> tagUpVotes = null;

        if(materialID != null) {
            Material material = materialService.get(materialID, getLoggedInUser());
            tagUpVotes = tagUpVoteService.getMaterialTagUpVotes(material, getLoggedInUser());
        } else if (portfolioID != null){
            Portfolio portfolio = portfolioService.get(portfolioID, getLoggedInUser());
            tagUpVotes = tagUpVoteService.getPortfolioTagUpVotes(portfolio, getLoggedInUser());
        }

        return tagUpVotes;
    }

    @DELETE
    public void removeUpVote(@QueryParam("material") Long materialID, @QueryParam("tag") String tagString, @QueryParam("portfolio") Long portfolioID) {
        Tag tag = null;
        if(tagString != null) {
            tag = tagService.getTagByName(tagString);
        }

        if(materialID != null && tag != null) {
            Material material = materialService.get(materialID, getLoggedInUser());
            tagUpVoteService.removeUpVoteFromMaterial(tag, material, getLoggedInUser());
        } else if (portfolioID != null && tag != null){
            Portfolio portfolio = portfolioService.get(portfolioID, getLoggedInUser());
            tagUpVoteService.removeUpVoteFromPortfolio(tag, portfolio, getLoggedInUser());
        }
    }


}
