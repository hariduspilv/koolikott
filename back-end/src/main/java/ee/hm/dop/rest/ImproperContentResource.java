package ee.hm.dop.rest;

import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.reviewmanagement.ImproperContentService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("impropers")
public class ImproperContentResource extends BaseResource {

    @Inject
    private ImproperContentService improperContentService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR, RoleString.RESTRICTED})
    public ImproperContent setImproper(ImproperContent improperContent) {
        return improperContentService.save(improperContent, getLoggedInUser(), improperContent.getLearningObject());
    }

    @PUT
    @Path("setImproper")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR, RoleString.RESTRICTED})
    public ImproperContent setImproper2(ImproperContentForm improperContent) {
        return improperContentService.save(improperContent.getImproperContent(), getLoggedInUser(), improperContent.getLearningObject());
    }

    public static class ImproperContentForm {
        private ImproperContent improperContent;
        private LearningObject learningObject;

        public ImproperContent getImproperContent() {
            return improperContent;
        }

        public void setImproperContent(ImproperContent improperContent) {
            this.improperContent = improperContent;
        }

        public LearningObject getLearningObject() {
            return learningObject;
        }

        public void setLearningObject(LearningObject learningObject) {
            this.learningObject = learningObject;
        }
    }
}
