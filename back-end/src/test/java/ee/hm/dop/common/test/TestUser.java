package ee.hm.dop.common.test;

public class TestUser {

    public TestUser(Long id, String idCode, String username) {
        this.idCode = idCode;
        this.id = id;
        this.username = username;
    }

    public TestUser(Long id, String idCode, String username, String firstName, String lastName) {
        this.id = id;
        this.idCode = idCode;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long id;
    public String idCode;
    public String username;
    public String firstName;
    public String lastName;
}
