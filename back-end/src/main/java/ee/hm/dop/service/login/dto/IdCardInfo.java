package ee.hm.dop.service.login.dto;

public class IdCardInfo {

    private String firstName;
    private String surName;
    private String idCode;

    public IdCardInfo(String firstName, String surName, String idCode) {
        this.firstName = firstName;
        this.surName = surName;
        this.idCode = idCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    @Override
    public String toString() {
        return "IdCardInfo{" +
                "firstName='" + firstName + '\'' +
                ", surName='" + surName + '\'' +
                ", idCode='" + idCode + '\'' +
                '}';
    }
}
