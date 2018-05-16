package ee.hm.dop.service.login.dto;

import ee.hm.dop.model.AuthenticatedUser;

public class UserStatus {

    private boolean status;
    private boolean userConfirmed;
    private String token;
    private Long agreementId;
    private AuthenticatedUser authenticatedUser;

    public static UserStatus missingPermissions(String token, Long agreementId){
        UserStatus status = new UserStatus();
        status.setStatus(false);
        status.setToken(token);
        status.setAgreementId(agreementId);
        return status;
    }

    public static UserStatus loggedIn(AuthenticatedUser authenticatedUser){
        UserStatus status = new UserStatus();
        status.setStatus(true);
        status.setAuthenticatedUser(authenticatedUser);
        return status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(Long agreementId) {
        this.agreementId = agreementId;
    }

    public AuthenticatedUser getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    public boolean isUserConfirmed() {
        return userConfirmed;
    }

    public void setUserConfirmed(boolean userConfirmed) {
        this.userConfirmed = userConfirmed;
    }
}
