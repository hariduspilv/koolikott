package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.model.LearningObject;

public enum ChangeProcessStrategy {
    REGISTER_NEW_CHANGES,
    DO_NOT_REGISTER_NEW_CHANGES;

    public static ChangeProcessStrategy processStrategy(LearningObject learningObject) {
        if (learningObject.getUnReviewed() == 0 && learningObject.getImproper() == 0 && learningObject.getBroken() == 0) {
            return REGISTER_NEW_CHANGES;
        }
        return DO_NOT_REGISTER_NEW_CHANGES;
    }

    public boolean processNewChanges() {
        return this == REGISTER_NEW_CHANGES;
    }
}
