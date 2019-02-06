package ee.netgroup.htm.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = ANY)
public class User {
    private Long id;
    private String username;
    private String name;
    private String surname;
    private UserRole role;
    private String location;
    private boolean newUser;
    private Publisher publisher;
    private List<Taxon> userTaxons;
    private List<UserAgreement> userAgreements;

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public void setUserTaxons(List<Taxon> userTaxons) {
        this.userTaxons = userTaxons;
    }

    public void setUserAgreements(List<UserAgreement> userAgreements) {
        this.userAgreements = userAgreements;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public List<Taxon> getUserTaxons() {
        return userTaxons;
    }

    public List<UserAgreement> getUserAgreements() {
        return userAgreements;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserRole getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }


    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public boolean isNewUser() {
        return newUser;
    }
}
