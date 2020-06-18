package ee.hm.dop.model.administration;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class PageableQueryUsers extends PageableQuery {

    private static final String BY_ROLE = "byRole";
    private static final String BY_ROLE_DESC = "-byRole";
    private static final String BY_EMAIL = "byEmail";
    private static final String BY_EMAIL_DESC = "-byEmail";
    private static final String BY_FULL_NAME = "byFullName";
    private static final String BY_FULL_NAME_DESC = "-byFullName";
    private static final String BY_USER_ROLE = "byUserRole";
    private static final String BY_USER_ROLE_DESC = "-byUserRole";
    private static final String BY_LOGIN = "byLogin";
    private static final String BY_LOGIN_DESC = "-byLogin";
    private String role;
    private String userRole;
    private String userEducationalContext;
    private boolean withEmail;
    private boolean withoutEmail;
    private String languageCode;
    private Integer limit;

    private static List<String> SORT_TYPES =
            Arrays.asList(BY_ROLE, BY_ROLE_DESC, BY_EMAIL, BY_EMAIL_DESC, BY_FULL_NAME, BY_FULL_NAME_DESC,
                    BY_USER_ROLE, BY_USER_ROLE_DESC, BY_LOGIN, BY_LOGIN_DESC);

    public PageableQueryUsers(int page, String itemSortedBy, String query, String role, String userRole,
                              String userEducationalContext, boolean withEmail, boolean withoutEmail, String languageCode, Integer limit) {

        if (itemSortedBy != null && SORT_TYPES.contains(itemSortedBy)) {
            valid = true;
            sort = itemSortedBy.startsWith("-") ? Sort.DESC : Sort.ASC;
            this.itemSortedBy = itemSortedBy;
            this.query = query;
            this.page = page;
            this.role = role;
            this.userRole = userRole;
            this.userEducationalContext = userEducationalContext;
            this.withEmail = withEmail;
            this.withoutEmail = withoutEmail;
            this.languageCode = languageCode;
            this.size = limit;
        } else {
            valid = false;
        }
    }

    private boolean orderByRole() {
        return itemSortedBy.equals(BY_ROLE);
    }

    private boolean orderByRoleDesc() {
        return itemSortedBy.equals(BY_ROLE_DESC);
    }

    private boolean orderByEmail() {
        return itemSortedBy.equals(BY_EMAIL);
    }

    private boolean orderByEmailDesc() {
        return itemSortedBy.equals(BY_EMAIL_DESC);
    }

    private boolean orderByFullName() {
        return itemSortedBy.equals(BY_FULL_NAME);
    }

    private boolean orderByFullNameDesc() {
        return itemSortedBy.equals(BY_FULL_NAME_DESC);
    }

    private boolean orderByUserRole() {
        return itemSortedBy.equals(BY_USER_ROLE);
    }

    private boolean orderByUserRoleDesc() {
        return itemSortedBy.equals(BY_USER_ROLE_DESC);
    }

    private boolean orderByLogin() {
        return itemSortedBy.equals(BY_LOGIN);
    }

    private boolean orderByLoginDesc() {
        return itemSortedBy.equals(BY_LOGIN_DESC);
    }

    public String group() {
        return "GROUP BY u.id, ue.email ";
    }

    public String order() {
        if (orderByRole() || orderByRoleDesc()) {
            return "ORDER BY tl.translation " + sort.name();
        } else if (orderByEmail() || orderByEmailDesc()) {
            return "ORDER BY if (email = '' or email is null,1,0),email " + sort.name();
        } else if (orderByFullName() || orderByFullNameDesc()) {
            return "ORDER BY u.name " + sort.name() + ", u.surName " + sort.name();
        } else if (orderByUserRole() || orderByUserRoleDesc()) {
            return "ORDER BY if (max(tl2.translation)  = '' or max(tl2.translation)  is null,1,0),max(tl2.translation) " + sort.name();
        } else if (orderByLogin() || orderByLoginDesc()) {
            return "ORDER BY if (max(au.loginDate)  = '' or max(au.loginDate)  is null,1,0),max(au.loginDate) " + sort.name() ;
        } else throw new UnsupportedOperationException("unknown sort");
    }
}
