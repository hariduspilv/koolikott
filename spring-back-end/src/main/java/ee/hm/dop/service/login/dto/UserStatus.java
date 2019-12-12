package ee.hm.dop.service.login.dto;

import ee.hm.dop.model.Agreement;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.enums.LoginFrom;

public class UserStatus {

    private boolean eKoolUserMissingIdCode;
    private boolean stuudiumUserMissingIdCode;
    private boolean harIdUserMissingIdCode;
    private boolean statusOk;
    private boolean existingUser;
    private boolean userConfirmed;
    private String token;
    private Agreement termsAgreement;
    private Agreement personalDataAgreement;
    private AuthenticatedUser authenticatedUser;
    private LoginFrom loginFrom;

    public static UserStatus missingPermissionsNewUser(String token, Agreement termsAgreement, Agreement personalDataAgreement, LoginFrom loginFrom) {
        UserStatus status = new UserStatus();
        status.setStatusOk(false);
        status.setToken(token);
        status.setTermsAgreement(termsAgreement);
        status.setPersonalDataAgreement(personalDataAgreement);
        status.setLoginFrom(loginFrom);
        return status;
    }

    public static UserStatus missingPermissionsExistingUser(String token, Agreement termsAgreement, Agreement personalDataAgreement, LoginFrom loginFrom) {
        UserStatus status = new UserStatus();
        status.setStatusOk(false);
        status.setToken(token);
        status.setTermsAgreement(termsAgreement);
        status.setPersonalDataAgreement(personalDataAgreement);
        status.setExistingUser(true);
        status.setLoginFrom(loginFrom);
        return status;
    }

    public static UserStatus missingTermsAgreement(String token, Agreement termsAgreement, LoginFrom loginFrom) {
        UserStatus status = new UserStatus();
        status.setStatusOk(false);
        status.setToken(token);
        status.setTermsAgreement(termsAgreement);
        status.setExistingUser(true);
        status.setLoginFrom(loginFrom);
        return status;
    }

    public static UserStatus missingPersonalDataAgreement(String token, Agreement personalDataAgreement, LoginFrom loginFrom) {
        UserStatus status = new UserStatus();
        status.setStatusOk(false);
        status.setToken(token);
        status.setPersonalDataAgreement(personalDataAgreement);
        status.setExistingUser(true);
        status.setLoginFrom(loginFrom);
        return status;
    }

    public static UserStatus loggedIn(AuthenticatedUser authenticatedUser) {
        UserStatus status = new UserStatus();
        status.setStatusOk(true);
        status.setAuthenticatedUser(authenticatedUser);
        return status;
    }

    public static UserStatus missingEkoolIdCode() {
        UserStatus userStatus = new UserStatus();
        userStatus.seteKoolUserMissingIdCode(true);
        return userStatus;
    }

    public static UserStatus missingStuudiumIdCode() {
        UserStatus userStatus = new UserStatus();
        userStatus.setStuudiumUserMissingIdCode(true);
        return userStatus;
    }

    public static UserStatus missingHarIdCode() {
        UserStatus userStatus = new UserStatus();
        userStatus.setHarIdUserMissingIdCode(true);
        return userStatus;
    }

    public boolean isStatusOk() {
        return statusOk;
    }

    public void setStatusOk(boolean statusOk) {
        this.statusOk = statusOk;
    }

    public boolean iseKoolUserMissingIdCode() {
        return eKoolUserMissingIdCode;
    }

    public boolean isStuudiumUserMissingIdCode() {
        return stuudiumUserMissingIdCode;
    }

    public void seteKoolUserMissingIdCode(boolean eKoolUserMissingIdCode) {
        this.eKoolUserMissingIdCode = eKoolUserMissingIdCode;
    }

    public void setStuudiumUserMissingIdCode(boolean stuudiumUserMissingIdCode) {
        this.stuudiumUserMissingIdCode = stuudiumUserMissingIdCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Agreement getTermsAgreement() {
        return termsAgreement;
    }

    public void setTermsAgreement(Agreement termsAgreement) {
        this.termsAgreement = termsAgreement;
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

    public LoginFrom getLoginFrom() {
        return loginFrom;
    }

    public void setLoginFrom(LoginFrom loginFrom) {
        this.loginFrom = loginFrom;
    }

    public boolean isHarIdUserMissingIdCode() {
        return harIdUserMissingIdCode;
    }

    public void setHarIdUserMissingIdCode(boolean harIdUserMissingIdCode) {
        this.harIdUserMissingIdCode = harIdUserMissingIdCode;
    }

    public Agreement getPersonalDataAgreement() {
        return personalDataAgreement;
    }

    public void setPersonalDataAgreement(Agreement personalDataAgreement) {
        this.personalDataAgreement = personalDataAgreement;
    }
}
