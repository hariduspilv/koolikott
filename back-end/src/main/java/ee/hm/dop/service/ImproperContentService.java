package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.ImproperContentDAO;
import ee.hm.dop.model.ImproperContent;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.User;
import org.joda.time.DateTime;

public class ImproperContentService {

    @Inject
    private ImproperContentDAO improperContentDAO;

    @Inject
    private LearningObjectService learningObjectService;

    public ImproperContent addImproper(ImproperContent improperContent, User creator) {
        if (improperContent == null || improperContent.getLearningObject() == null) {
            throw new RuntimeException("Invalid Improper object.");
        }

        LearningObject learningObject = learningObjectService.get(improperContent.getLearningObject().getId(), creator);

        if (learningObject == null) {
            throw new RuntimeException("LearningObject does not exists.");
        }

        ImproperContent improper = new ImproperContent();
        improper.setCreator(creator);
        improper.setAdded(DateTime.now());
        improper.setLearningObject(learningObject);
        improper.setReason(improperContent.getReason());

        return improperContentDAO.update(improper);
    }

    /**
     * @param improperContentId
     * @param user
     *            who wants access to the list
     * @return the ImproperContent if user has rights to access
     */
    public ImproperContent get(long improperContentId, User user) {
        ImproperContent improperContent = improperContentDAO.findById(improperContentId);

        if (improperContent != null && !learningObjectService.hasAccess(user, improperContent.getLearningObject())) {
            improperContent = null;
        }

        return improperContent;
    }

    /**
     * @param user
     *            who wants access to the list
     * @return a list of improperContent that user has rights to access
     */
    public List<ImproperContent> getAll(User user) {
        List<ImproperContent> impropers = improperContentDAO.findAll();
        removeIfHasNoAccess(user, impropers);

        return impropers;
    }

    /**
     * 
     * @param learningObject
     * @param creator
     *            who created the ImproperContent
     * @param user
     *            who wants access to the list
     * @return the ImproperContent which refers to learningObject, created by
     *         creator and user has rights to access
     */
    public ImproperContent getByLearningObjectAndCreator(LearningObject learningObject, User creator, User user) {
        ImproperContent improperContent = improperContentDAO.findByLearningObjectAndCreator(learningObject, creator);

        if (improperContent != null && !learningObjectService.hasAccess(user, improperContent.getLearningObject())) {
            improperContent = null;
        }

        return improperContent;
    }

    /**
     * @param user
     *            who wants access to the list
     * @return a list of improperContent which refers to learningObject and user
     *         has rights to access
     */
    public List<ImproperContent> getByLearningObject(LearningObject learningObject, User user) {
        List<ImproperContent> impropers = improperContentDAO.findByLearningObject(learningObject);
        removeIfHasNoAccess(user, impropers);

        return impropers;
    }

    /**
     * @param impropers
     *            the list of ImproperContent to be deleted
     * @param user
     *            who wants to delete the improper
     */
    public void deleteAll(List<ImproperContent> impropers, User user) {
        removeIfHasNoAccess(user, impropers);
        improperContentDAO.deleteAll(impropers);
    }

    private void removeIfHasNoAccess(User user, List<ImproperContent> impropers) {
        impropers.removeIf(improper -> !learningObjectService.hasAccess(user, improper.getLearningObject()));
    }
}
