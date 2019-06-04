package ee.hm.dop.service.useractions;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.utils.ValidatorUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
@Transactional
public class CommentService {

    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private LearningObjectDao learningObjectDao;

    public LearningObject addComment(Comment comment, LearningObject learningObject, User loggedInUser) {
        mustBeValidComment(comment);

        LearningObject originalLearningObject = learningObjectService.validateAndFind(learningObject);

        if (!learningObjectService.canView(loggedInUser, originalLearningObject)) {
            throw ValidatorUtil.permissionError();
        }

        comment.setCreator(loggedInUser);
        comment.setAdded(LocalDateTime.now());
        originalLearningObject.getComments().add(0, comment);
        learningObjectDao.createOrUpdate(originalLearningObject);
        return originalLearningObject;
    }

    private void mustBeValidComment(Comment comment) {
        if (isEmpty(comment.getText()) || comment.getId() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment is missing text or already exists.");
    }
}
