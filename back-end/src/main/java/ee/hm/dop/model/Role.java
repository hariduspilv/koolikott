package ee.hm.dop.model;

public enum Role {
    USER, ADMIN, RESTRICTED, MODERATOR;

    public static Role getEnumByString(String enumString) {
        switch (enumString) {
            case "USER":
                return USER;
            case "ADMIN":
                return ADMIN;
            case "RESTRICTED":
                return RESTRICTED;
            case "MODERATOR":
                return MODERATOR;
        }

        return null;
    }
}
