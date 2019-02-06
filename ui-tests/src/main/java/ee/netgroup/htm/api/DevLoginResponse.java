package ee.netgroup.htm.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

@JsonAutoDetect(fieldVisibility = ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DevLoginResponse {
    private AuthenticatedUser authenticatedUser;
    private Boolean statusOk;
    private Boolean existingUser;
    private Boolean userConfirmed;
    private String token;
    private Integer agreementId;

    public void setAuthenticatedUser(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    public void setStatusOk(Boolean statusOk) {
        this.statusOk = statusOk;
    }

    public void setExistingUser(Boolean existingUser) {
        this.existingUser = existingUser;
    }

    public void setUserConfirmed(Boolean userConfirmed) {
        this.userConfirmed = userConfirmed;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAgreementId(Integer agreementId) {
        this.agreementId = agreementId;
    }

    public AuthenticatedUser getAuthenticatedUserDto() {
        return authenticatedUser;
    }

    public Boolean getStatusOk() {
        return statusOk;
    }

    public Boolean getExistingUser() {
        return existingUser;
    }

    public Boolean getUserConfirmed() {
        return userConfirmed;
    }

    public String getToken() {
        return token;
    }

    public Integer getAgreementId() {
        return agreementId;
    }
}
