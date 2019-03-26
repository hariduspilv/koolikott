package ee.hm.dop.model.enums;

public enum UserRole {
    STUDENT, TEACHER, PARENT, OTHER;

    public boolean needsInstitutions() {
        return this == STUDENT || this == TEACHER;
    }
}
