package ee.hm.dop.model.harid;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HarIdUser {

    @JsonProperty("personal_code")
    private String idCode;

    @JsonProperty("given_name")
    private String firstName;

    @JsonProperty("family_name")
    private String lastName;

    @JsonProperty("strong_session")
    private String strongSession;

    public HarIdUser(String idCode, String firstName, String lastName) {
        this.idCode = idCode;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public HarIdUser() {
    }

    public HarIdUser(String idCode) {
        this.idCode = idCode;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStrongSession() {
        return strongSession;
    }

    public void setStrongSession(String strongSession) {
        this.strongSession = strongSession;
    }
}
