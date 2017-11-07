package ee.hm.dop.rest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.useractions.TagUpVoteService;

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
            throw badRequest("TagUpVote already exists.");
        }
        LearningObject learningObject = learningObjectService.get(tagUpVote.getLearningObject().getId(), getLoggedInUser());
        if (learningObject == null) {
            throw badRequest("No such learning object");
        }
        Tag tag = tagService.getTagByName(tagUpVote.getTag().getName());
        if (tag == null) {
            throw badRequest("No such tag");
        }

        TagUpVote trustTagUpVote = new TagUpVote();
        trustTagUpVote.setLearningObject(learningObject);
        trustTagUpVote.setTag(tag);
        trustTagUpVote.setUser(tagUpVote.getUser());

        return tagUpVoteService.upVote(trustTagUpVote, getLoggedInUser());
    }

    @GET
    @Path("report")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<TagUpVoteForm> getTagUpVotesReport(@QueryParam("learningObject") Long learningObjectId) {
        if (learningObjectId == null) {
            throw badRequest("LearningObject query param is required");
        }
        User user = getLoggedInUser();
        LearningObject learningObject = learningObjectService.get(learningObjectId, user);
        if (learningObject != null) {
            return convertForms(user, learningObject);
        }
        return Collections.emptyList();
    }

    @DELETE
    @Path("{tagUpVoteId}")
    public void removeUpVote(@PathParam("tagUpVoteId") long tagUpVoteId) {
        TagUpVote tagUpVote = tagUpVoteService.get(tagUpVoteId, getLoggedInUser());
        if (tagUpVote == null) {
            throw notFound();
        }
        tagUpVoteService.delete(tagUpVote, getLoggedInUser());
    }

    private List<TagUpVoteForm> convertForms(User user, LearningObject learningObject) {
        return learningObject.getTags().stream()
                .map(tag -> convertForm(user, learningObject, tag))
                .collect(Collectors.toList());
    }

    private TagUpVoteForm convertForm(User user, LearningObject learningObject, Tag tag) {
        TagUpVoteForm form = new TagUpVoteForm();
        form.tag = tag;
        form.upVoteCount = tagUpVoteService.getUpVoteCountFor(tag, learningObject);
        if (form.upVoteCount > 0) {
            form.tagUpVote = tagUpVoteService.getTagUpVote(tag, learningObject, user);
        }
        return form;
    }

    public static class TagUpVoteForm {
        private Tag tag;
        private long upVoteCount;
        private TagUpVote tagUpVote;

        public Tag getTag() {
            return tag;
        }

        public long getUpVoteCount() {
            return upVoteCount;
        }

        public TagUpVote getTagUpVote() {
            return tagUpVote;
        }
    }
}
