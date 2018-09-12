package ee.hm.dop.service.login.dto;

import ee.hm.dop.model.AuthenticatedUser;

public class UserStatus {

    private boolean eKoolLoginMissingIdCode;
    private boolean stuudiumLoginMissingIdCode;
    private boolean statusOk;
    private boolean existingUser;
    private boolean userConfirmed;
    private String token;
    private Long agreementId;
    private AuthenticatedUser authenticatedUser;

    public static UserStatus missingPermissionsNewUser(String token, Long agreementId){
        UserStatus status = new UserStatus();
        status.setStatusOk(false);
        status.setToken(token);
        status.setAgreementId(agreementId);
        return status;
    }

    public static UserStatus missingPermissionsExistingUser(String token, Long agreementId){
        UserStatus status = new UserStatus();
        status.setStatusOk(false);
        status.setToken(token);
        status.setAgreementId(agreementId);
        status.setExistingUser(true);
        return status;
    }

    public static UserStatus loggedIn(AuthenticatedUser authenticatedUser){
        UserStatus status = new UserStatus();
        status.setStatusOk(true);
        status.setAuthenticatedUser(authenticatedUser);
        return status;
    }

    public static UserStatus missingEkoolIdCode() {
        UserStatus userStatus = new UserStatus();
        userStatus.seteKoolLoginMissingIdCode(true);
        return userStatus;
    }

    public static UserStatus missingStuudiumIdCode() {
        UserStatus userStatus = new UserStatus();
        userStatus.setStuudiumLoginMissingIdCode(true);
        return userStatus;
    }

    public boolean isStatusOk() {
        return statusOk;
    }

    public void setStatusOk(boolean statusOk) {
        this.statusOk = statusOk;
    }

    public boolean iseKoolLoginMissingIdCode() {
        return eKoolLoginMissingIdCode;
    }

    public boolean isStuudiumLoginMissingIdCode() {
        return stuudiumLoginMissingIdCode;
    }

    public void seteKoolLoginMissingIdCode(boolean eKoolLoginMissingIdCode) {
        this.eKoolLoginMissingIdCode = eKoolLoginMissingIdCode;
    }

    public void setStuudiumLoginMissingIdCode(boolean stuudiumLoginMissingIdCode) {
        this.stuudiumLoginMissingIdCode = stuudiumLoginMissingIdCode;
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

    public boolean isExistingUser() {
        return existingUser;
    }

    public void setExistingUser(boolean existingUser) {
        this.existingUser = existingUser;
    }
}
