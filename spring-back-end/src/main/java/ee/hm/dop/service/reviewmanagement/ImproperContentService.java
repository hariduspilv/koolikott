package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.dao.ImproperContentDao;
import ee.hm.dop.dao.ReportingReasonDao;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.ReportingReason;
import ee.hm.dop.model.User;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.utils.UserUtil;
import ee.hm.dop.utils.ValidatorUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ImproperContentService {

    @Inject
    private ImproperContentDao improperContentDao;
    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private ReportingReasonDao reportingReasonDao;

    public List<ImproperContent> getImproperContent(long learningObjectId, User loggedInUser) {
        UserUtil.mustBeModeratorOrAdmin(loggedInUser);
        return improperContentDao.findByLearningObjectId(learningObjectId);
    }

    public ImproperContent save(ImproperContent improperContent, User creator, LearningObject learningObject1) {
        LearningObject originalLearningObject = findValid(improperContent, creator, learningObject1);

        ImproperContent improper = new ImproperContent();
        improper.setCreatedBy(creator);
        improper.setCreatedAt(LocalDateTime.now());
        improper.setLearningObject(originalLearningObject);
        improper.setReportingText(improperContent.getReportingText());
        improper.setReviewed(false);
        improper.setReportingReasons(new ArrayList<>());

        ImproperContent saved = improperContentDao.createOrUpdate(improper);
        for (ReportingReason reason : improperContent.getReportingReasons()) {
            if (reason.getId() == null) {
                saveReason(saved, reason);
            }
        }
        if (CollectionUtils.isEmpty(saved.getReportingReasons())) {
            throw badRequest("no reason specified");
        }
        improperContentDao.createOrUpdate(saved);
        return saved;
    }

    private void saveReason(ImproperContent improperContent, ReportingReason reasonDto) {
        ReportingReason reasonDb = new ReportingReason();
        reasonDb.setImproperContent(improperContent);
        reasonDb.setReason(reasonDto.getReason());
        ReportingReason saved = reportingReasonDao.createOrUpdate(reasonDb);
        improperContent.getReportingReasons().add(saved);
    }

    private LearningObject findValid(ImproperContent improperContent, User creator, LearningObject learningObject1) {
        if (improperContent == null || learningObject1 == null) {
            throw badRequest("Invalid Improper object.");
        }
        LearningObject learningObject = learningObjectService.get(learningObject1.getId(), creator);
        ValidatorUtil.mustHaveEntity(learningObject, learningObject1.getId());
        return learningObject;
    }

    private ResponseStatusException badRequest(String s) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, s);
    }

}
