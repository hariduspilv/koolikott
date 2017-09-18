package ee.hm.dop.service.content.enums;

public enum  GetMaterialStrategy {
    ONLY_EXISTING,
    /**
     * Admin can search for deleted materials too, as admin can restore them.
     */
    INCLUDE_DELETED;

    public boolean isDeleted(){
        return this == INCLUDE_DELETED;
    }
}
