package ee.hm.dop.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.*;
import ee.hm.dop.service.LearningObjectService;
import ee.hm.dop.service.TagService;
import ee.hm.dop.service.TagUpVoteService;

@Path("tagUpVotes")
@RolesAllowed({ RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR })
public class TagUpVoteResource extends BaseResource {

    @Inject
    private TagUpVoteService tagUpVoteService;

    @Inject
    private LearningObjectService learningObjectService;

    @Inject
    private TagService tagService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TagUpVote upVote(TagUpVote tagUpVote) {
        if (tagUpVote.getId() != null) {
            throwBadRequestException("TagUpVote already exists.");
        }

        LearningObject learningObject = learningObjectService.get(tagUpVote.getLearningObject().getId(),
                getLoggedInUser());
        if (learningObject == null) {
            throwBadRequestException("No such learning object");
        }

        Tag tag = tagService.getTagByName(tagUpVote.getTag().getName());
        if (tag == null) {
            throwBadRequestException("No such tag");
        }

        TagUpVote trustTagUpVote = new TagUpVote();
        trustTagUpVote.setLearningObject(learningObject);
        trustTagUpVote.setTag(tag);
        trustTagUpVote.setUser(tagUpVote.getUser());

        return tagUpVoteService.upVote(tagUpVote, getLoggedInUser());
    }

    @GET
    @Path("report")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<TagUpVoteForm> getTagUpVotesReport(@QueryParam("learningObject") Long learningObjectId) {
        if (learningObjectId == null) {
            throwBadRequestException("LearningObject query param is required");
        }

        List<TagUpVoteForm> tagUpVotes = new ArrayList<>();

        User user = getLoggedInUser();
        LearningObject learningObject = learningObjectService.get(learningObjectId, user);
        if (learningObject != null) {
            for (Tag tag : learningObject.getTags()) {
                TagUpVoteForm form = new TagUpVoteForm();
                form.tag = tag;
                form.upVoteCount = tagUpVoteService.getUpVoteCountFor(tag, learningObject);

                if (form.upVoteCount > 0) {
                    form.tagUpVote = tagUpVoteService.getTagUpVote(tag, learningObject, user);
                }

                tagUpVotes.add(form);
            }
        }

        return tagUpVotes;
    }

    @DELETE
    @Path("{tagUpVoteId}")
    public void removeUpVote(@PathParam("tagUpVoteId") long tagUpVoteId) {
        TagUpVote tagUpVote = tagUpVoteService.get(tagUpVoteId, getLoggedInUser());
        if (tagUpVote == null) {
            throwNotFoundException();
        }

        tagUpVoteService.delete(tagUpVote, getLoggedInUser());
    }

    public static class TagUpVoteForm {

        private Tag tag;
        private int upVoteCount;
        private TagUpVote tagUpVote;

        public Tag getTag() {
            return tag;
        }

        public int getUpVoteCount() {
            return upVoteCount;
        }

        public TagUpVote getTagUpVote() {
            return tagUpVote;
        }
    }
}
