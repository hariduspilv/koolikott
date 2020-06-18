package ee.hm.dop.model;

import lombok.AllArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
public class UserDto {

    public Long id;
    public String firstName;
    public String lastName;
    public String email;
    public Boolean emailActivated;
    public String customRole;
    public String applicationRole;
    public String userProfileRole;
    public Timestamp lastLogin;
    public List<String> educationalContexts;
    public List<String> domains;

}
