package ee.hm.dop.model.enums;

import java.util.List;

public enum Role {
    USER, ADMIN, RESTRICTED, MODERATOR;

    public String getRole() {
        return "ROLE_" + this.name();
    }
}
