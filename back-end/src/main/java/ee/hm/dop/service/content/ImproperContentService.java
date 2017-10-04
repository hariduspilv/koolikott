package ee.hm.dop.service.content;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.ImproperContentDao;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class ImproperContentService {

    @Inject
    private ImproperContentDao improperContentDao;
    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private FirstReviewService firstReviewService;

    public List<ImproperContent> getImproperContent(long learningObjectId, User loggedInUser) {
        LearningObject learningObject = learningObjectService.get(learningObjectId, loggedInUser);

        if (UserUtil.isAdmin(loggedInUser)) {
            return getByLearningObject(learningObject, loggedInUser);
        }
        ImproperContent improper = getByLearningObjectAndCreator(learningObject, loggedInUser, loggedInUser);
        return improper != null ? Lists.newArrayList(improper) : Lists.newArrayList();
    }

    public ImproperContent addImproper(ImproperContent improperContent, User creator) {
        LearningObject learningObject = findValid(improperContent, creator);

        ImproperContent improper = new ImproperContent();
        improper.setCreator(creator);
        improper.setCreatedAt(DateTime.now());
        improper.setLearningObject(learningObject);
        improper.setReason(improperContent.getReason());
        improper.setReviewed(false);

        return improperContentDao.createOrUpdate(improper);
    }

    private LearningObject findValid(ImproperContent improperContent, User creator) {
        if (improperContent == null || improperContent.getLearningObject() == null) {
            throw new RuntimeException("Invalid Improper object.");
        }
        LearningObject learningObject = learningObjectService.get(improperContent.getLearningObject().getId(), creator);
        ValidatorUtil.mustHaveEntity(learningObject);
        return learningObject;
    }

    /**
     * @param improperContentId
     * @param user              who wants access to the list
     * @return the ImproperContent if user has rights to access
     */
    public ImproperContent get(long improperContentId, User user) {
        ImproperContent improperContent = improperContentDao.findByIdUnreviewed(improperContentId);
        if (improperContent != null && !learningObjectService.canAcess(user, improperContent.getLearningObject())) {
            return null;
        }
        return improperContent;
    }

    /**
     * @param user who wants access to the list
     * @return a list of improperContent that user has rights to access
     */
    public List<ImproperContent> getAll(User user) {
        List<ImproperContent> impropers = improperContentDao.findAllUnreviewed();
        removeIfHasNoAccess(user, impropers);
        return impropers;
    }

    /**
     * @param learningObject
     * @param creator        who created the ImproperContent
     * @param user           who wants access to the list
     * @return the ImproperContent which refers to learningObject, created by
     * creator and user has rights to access
     */
    public ImproperContent getByLearningObjectAndCreator(LearningObject learningObject, User creator, User user) {
        ImproperContent improperContent = improperContentDao.findByLearningObjectAndCreator(learningObject, creator);
        if (improperContent != null && !learningObjectService.canAcess(user, improperContent.getLearningObject())) {
            return null;
        }
        return improperContent;
    }

    /**
     * @param user who wants access to the list
     * @return a list of improperContent which refers to learningObject and user
     * has rights to access
     */
    public List<ImproperContent> getByLearningObject(LearningObject learningObject, User user) {
        List<ImproperContent> impropers = improperContentDao.findByLearningObject(learningObject);
        removeIfHasNoAccess(user, impropers);
        return impropers;
    }

    /**
     * @param impropers the list of ImproperContent to be reviewed
     * @param user      who wants to review the improper content
     */
    public void reviewAll(List<ImproperContent> impropers, User user) {
        removeIfHasNoAccess(user, impropers);
        List<LearningObject> learningObjects = impropers.stream().map(ImproperContent::getLearningObject).distinct().collect(Collectors.toList());
        firstReviewService.setReviewed(learningObjects, user);
        for (ImproperContent improper : impropers) {
            improper.setReviewed(true);
            improper.setReviewedBy(user);
            improper.setReviewedAt(DateTime.now());
            improper.setStatus(ReviewStatus.ACCEPTED);
            improperContentDao.createOrUpdate(improper);
        }
    }

    public void reviewAll(List<ImproperContent> impropers, User user, ReviewStatus reviewStatus) {
        removeIfHasNoAccess(user, impropers);
        List<LearningObject> learningObjects = impropers.stream().map(ImproperContent::getLearningObject).distinct().collect(Collectors.toList());
        firstReviewService.setReviewed(learningObjects, user);
        for (ImproperContent improper : impropers) {
            improper.setReviewed(true);
            improper.setReviewedBy(user);
            improper.setReviewedAt(DateTime.now());
            improper.setStatus(reviewStatus);
            improperContentDao.createOrUpdate(improper);
        }
    }

    private void removeIfHasNoAccess(User user, List<ImproperContent> impropers) {
        impropers.removeIf(improper -> !learningObjectService.canAcess(user, improper.getLearningObject()));
    }
}
