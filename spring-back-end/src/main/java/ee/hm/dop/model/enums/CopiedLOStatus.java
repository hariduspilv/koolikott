package ee.hm.dop.model.enums;

public enum CopiedLOStatus {
    PRIVATE, DELETED;

    public boolean isPrivate() {
        return this == PRIVATE;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }
}

