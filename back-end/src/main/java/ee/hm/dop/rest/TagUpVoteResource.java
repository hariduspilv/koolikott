package ee.hm.dop.rest;

import ee.hm.dop.dao.LearningObjectLogDao;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.useractions.TagUpVoteService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Path("tagUpVotes")
@RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
public class TagUpVoteResource extends BaseResource {

    @Inject
    private TagUpVoteService tagUpVoteService;
    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private TagService tagService;
    @Inject
    private LearningObjectLogDao learningObjectLogDao;

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
    @PermitAll
    @Path("report")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TagUpVoteForm> getTagUpVotesReport(@QueryParam("learningObject") Long learningObjectId, @QueryParam("portfolioLog") boolean portfolioLog) {
        if (learningObjectId == null) {
            throw badRequest("LearningObject query param is required");
        }
        User user = getLoggedInUser();

        if (!portfolioLog) {
            LearningObject learningObject = learningObjectService.get(learningObjectId, user);
            if (learningObject != null) {
                return convertForms(user, learningObject);
            }
        } else {
            LearningObjectLog learningObjectLog = learningObjectLogDao.findById(learningObjectId);
            if (learningObjectLog != null) {
                return convertForms(user, learningObjectLog);
            }
        }
        return Collections.emptyList();
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void removeUpVote(TagUpVote tagUpVote) {
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

    private List<TagUpVoteForm> convertForms(User user, LearningObjectLog learningObject) {
        return learningObject
                .getTags()
                .stream()
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
    private TagUpVoteForm convertForm(User user, LearningObjectLog learningObject, Tag tag) { //TODO
        TagUpVoteForm form = new TagUpVoteForm();
        form.tag = tag;
//        form.upVoteCount = tagUpVoteService.getUpVoteCountFor(tag, learningObject);
//        if (form.upVoteCount > 0) {
//            form.tagUpVote = tagUpVoteService.getTagUpVote(tag, learningObject, user);
//        }
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
