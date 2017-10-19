package ee.hm.dop.service.useractions;

import ee.hm.dop.dao.LearningObjectDao;
import ee.hm.dop.model.Comment;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.User;
import ee.hm.dop.service.content.LearningObjectService;
import ee.hm.dop.utils.ValidatorUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class CommentService {

    @Inject
    private LearningObjectService learningObjectService;
    @Inject
    private LearningObjectDao learningObjectDao;

    public void addComment(Comment comment, LearningObject learningObject, User loggedInUser) {
<<<<<<< HEAD
        if (isEmpty(comment.getText()) || comment.getId() != null)
            throw new RuntimeException("Comment is missing text or already exists.");
=======
        mustBeValidComment(comment);
>>>>>>> new-develop

        LearningObject originalLearningObject = learningObjectService.validateAndFind(learningObject);

        if (!learningObjectService.canView(loggedInUser, originalLearningObject)) {
            throw ValidatorUtil.permissionError();
        }

<<<<<<< HEAD
=======
        comment.setCreator(loggedInUser);
>>>>>>> new-develop
        comment.setAdded(DateTime.now());
        originalLearningObject.getComments().add(comment);
        learningObjectDao.createOrUpdate(originalLearningObject);
    }
<<<<<<< HEAD
=======

    private void mustBeValidComment(Comment comment) {
        if (isEmpty(comment.getText()) || comment.getId() != null)
            throw new RuntimeException("Comment is missing text or already exists.");
    }
>>>>>>> new-develop
}
