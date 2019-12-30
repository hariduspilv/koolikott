package ee.hm.dop.service.login.dto;

import ee.hm.dop.model.Agreement;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.enums.LoginFrom;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatus {

    private boolean eKoolUserMissingIdCode;
    private boolean stuudiumUserMissingIdCode;
    private boolean harIdUserMissingIdCode;
    private boolean statusOk;
    private boolean existingUser;
    private boolean userConfirmed;
    private String token;
    private Agreement userTermsAgreement;
    private Agreement gdprTermsAgreement;
    private AuthenticatedUser authenticatedUser;
    private LoginFrom loginFrom;

    public static UserStatus missingPermissionsNewUser(String token, Agreement userTermsAgreement, Agreement gdprTermsAgreement, LoginFrom loginFrom) {
        UserStatus status = new UserStatus();
        status.setStatusOk(false);
        status.setToken(token);
        status.setUserTermsAgreement(userTermsAgreement);
        status.setGdprTermsAgreement(gdprTermsAgreement);
        status.setLoginFrom(loginFrom);
        return status;
    }

    public static UserStatus missingPermissionsExistingUser(String token, Agreement userTermsAgreement, Agreement gdprTermsAgreement, LoginFrom loginFrom) {
        UserStatus status = new UserStatus();
        status.setStatusOk(false);
        status.setToken(token);
        status.setUserTermsAgreement(userTermsAgreement);
        status.setGdprTermsAgreement(gdprTermsAgreement);
        status.setExistingUser(true);
        status.setLoginFrom(loginFrom);
        return status;
    }

    public static UserStatus missingTermsAgreement(String token, Agreement userTermsAgreement, LoginFrom loginFrom) {
        UserStatus status = new UserStatus();
        status.setStatusOk(false);
        status.setToken(token);
        status.setUserTermsAgreement(userTermsAgreement);
        status.setExistingUser(true);
        status.setLoginFrom(loginFrom);
        return status;
    }

    public static UserStatus missingGdprTermsAgreement(String token, Agreement gdprTermsAgreement, LoginFrom loginFrom) {
        UserStatus status = new UserStatus();
        status.setStatusOk(false);
        status.setToken(token);
        status.setGdprTermsAgreement(gdprTermsAgreement);
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
        userStatus.setEKoolUserMissingIdCode(true);
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
}
