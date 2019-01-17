package ee.hm.dop.model.stuudium;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StuudiumUser {

    @JsonProperty("idcode")
    private String idCode;

    @JsonProperty("name_first")
    private String firstName;

    @JsonProperty("name_last")
    private String lastName;

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

}
