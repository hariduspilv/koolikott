package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.ImproperContentDao;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.model.AdminLearningObject;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.utils.UserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ImproperContentAdminService {

    @Inject
    private ImproperContentDao improperContentDao;

    @Inject
    private TaxonDao taxonDao;

    public List<AdminLearningObject> getImproper(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return improperContentDao.findAllUnreviewed();
        }
        return improperContentDao.findAllUnreviewed(user);
    }

    public long getImproperCount(User user) {
        UserUtil.mustBeModeratorOrAdmin(user);
        if (UserUtil.isAdmin(user)) {
            return improperContentDao.findCountOfUnreviewed();
        }
        if (taxonDao.getUserTaxons(user).isEmpty()) {
            return 0;
        }
        return improperContentDao.findCountOfUnreviewed(user);
    }

    public void setReviewed(LearningObject learningObject, User user, ReviewStatus reviewStatus) {
        for (ImproperContent improperContent : learningObject.getImproperContents()) {
            if (!improperContent.isReviewed()) {
                setReviewed(user, reviewStatus, improperContent);
                learningObject.setImproper(learningObject.getImproper() - 1);
            }
        }
    }

    private void setReviewed(User user, ReviewStatus reviewStatus, ImproperContent improper) {
        improper.setReviewed(true);
        improper.setReviewedBy(user);
        improper.setReviewedAt(LocalDateTime.now());
        improper.setStatus(reviewStatus);
        improperContentDao.createOrUpdate(improper);
    }
}
