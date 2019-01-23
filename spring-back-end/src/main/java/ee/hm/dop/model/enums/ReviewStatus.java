package ee.hm.dop.model.enums;

public enum ReviewStatus {
    RESTORED, ACCEPTED, REJECTED, DELETED, ACCEPTED_AUTOMATICALLY,
    /**
     * as in another change has made this change obsolete
     */
    OBSOLETE
}
