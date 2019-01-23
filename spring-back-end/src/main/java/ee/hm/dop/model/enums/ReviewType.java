package ee.hm.dop.model.enums;

import java.util.Arrays;
import java.util.List;

public enum ReviewType {
    SYSTEM_RESTORE,
    SYSTEM_DELETE,
    IMPROPER,
    FIRST,
    CHANGE;

    private List<ReviewType> improperAndBrokenReviewList() {
        return Arrays.asList(SYSTEM_RESTORE, SYSTEM_DELETE, IMPROPER);
    }

    private List<ReviewType> firstReviewList() {
        return Arrays.asList(SYSTEM_RESTORE, SYSTEM_DELETE, IMPROPER, FIRST);
    }

    private List<ReviewType> changeReviewList() {
        return Arrays.asList(SYSTEM_RESTORE, SYSTEM_DELETE, IMPROPER, FIRST, CHANGE);
    }

    public boolean canReviewImproperContent(){
        return improperAndBrokenReviewList().contains(this);
    }

    public boolean canReviewFirstReview(){
        return firstReviewList().contains(this);
    }

    public boolean canReviewChange(){
        return changeReviewList().contains(this);
    }
}
