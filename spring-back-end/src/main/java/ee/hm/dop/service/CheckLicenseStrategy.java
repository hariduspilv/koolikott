package ee.hm.dop.service;

public enum CheckLicenseStrategy {
    USER_HAS_LOGGED_IN,
    FIRST_LOGIN;

    public boolean isFirstLogin() {
        return this == FIRST_LOGIN;
    }
}
