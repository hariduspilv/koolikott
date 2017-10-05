package ee.hm.dop.service.content;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.ImproperContentDao;
import ee.hm.dop.dao.ReportingReasonDao;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.ReportingReason;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.ReportingReasonEnum;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.List;

public class ImproperContentService {

    @Inject
    private ImproperContentDao improperContentDao;
    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private ReportingReasonDao reportingReasonDao;

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
        improper.setReportingText(improperContent.getReportingText());
        improper.setReviewed(false);

        ImproperContent create = improperContentDao.createOrUpdate(improper);
        if (CollectionUtils.isNotEmpty(improperContent.getReportingReasonEnums())) {
            for (ReportingReasonEnum reasonEnum : improperContent.getReportingReasonEnums()) {
                createReason(improper, create, reasonEnum);
            }
            create.setReportingReasonEnums(improperContent.getReportingReasonEnums());
        }
        return create;
    }

    private void createReason(ImproperContent improper, ImproperContent create, ReportingReasonEnum reasonEnum) {
        ReportingReason reason = new ReportingReason();
        reason.setImproperContent(improper);
        reason.setReason(reasonEnum);
        reportingReasonDao.createOrUpdate(reason);
        create.getReportingReasons().add(reason);
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
        if (improperContent != null && !learningObjectService.canAccess(user, improperContent.getLearningObject())) {
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

    public void setReviewed(LearningObject learningObject, User user, ReviewStatus reviewStatus) {
        for (ImproperContent improperContent : learningObject.getImproperContents()) {
            setReviewed(user, reviewStatus, improperContent);
        }
    }

    private void setReviewed(User user, ReviewStatus reviewStatus, ImproperContent improper) {
        improper.setReviewed(true);
        improper.setReviewedBy(user);
        improper.setReviewedAt(DateTime.now());
        improper.setStatus(reviewStatus);
        improperContentDao.createOrUpdate(improper);
    }

    private void removeIfHasNoAccess(User user, List<ImproperContent> impropers) {
        impropers.removeIf(improper -> !learningObjectService.canAccess(user, improper.getLearningObject()));
    }
}
