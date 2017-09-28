package ee.hm.dop.model.enums;

public enum Visibility {
    PUBLIC, NOT_LISTED, PRIVATE;

    public boolean isNotPrivate(){
        return this != PRIVATE;
    }
    public boolean isPublic(){
        return this == PUBLIC;
    }
}
