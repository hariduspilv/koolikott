package ee.hm.dop.rest;

import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.ImproperContentDto;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.reviewmanagement.ImproperContentService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("impropers")
public class ImproperContentResource extends BaseResource {

    @Inject
    private ImproperContentService improperContentService;

    @PutMapping
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR, RoleString.RESTRICTED})
    public ImproperContent setImproper(@RequestBody ImproperContent improperContent) {
        return improperContentService.save(improperContent, getLoggedInUser(), improperContent.getLearningObject());
    }

    @PutMapping
    @RequestMapping("setImproper")
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR, RoleString.RESTRICTED})
    public ImproperContent setImproper2(@RequestBody ImproperContentForm form) {
        return improperContentService.save(form.getImproperContent(), getLoggedInUser(), form.getLearningObject());
    }

    @GetMapping
    @RequestMapping("owner/{learningObjectId}")
    @Secured({RoleString.USER})
    public List<ImproperContentDto> getImproperContentOwner(@PathVariable("learningObjectId") Long learningObjectId) {
        return improperContentService.getImproperContentForOwner(learningObjectId, getLoggedInUser());
    }

    @GetMapping
    @RequestMapping("user/{learningObjectId}")
    public List<ImproperContentDto> getImproperContentUser(@PathVariable("learningObjectId") Long learningObjectId) {
        return improperContentService.getImproperContentForUser(learningObjectId);
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
