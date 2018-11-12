package ee.hm.dop.model;

import static javax.persistence.CascadeType.ALL;
import static org.joda.time.DateTime.now;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ee.hm.dop.model.ehis.Person;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
public class AuthenticatedUser implements AbstractEntity {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column
    private boolean firstLogin;

    @Column
    private boolean deleted;

    @OneToOne(cascade = ALL)
    @JoinColumn(name = "person")
    private Person person;

    @JsonIgnore
    @Column(nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime loginDate = now();

    public AuthenticatedUser() {
    }

    public AuthenticatedUser(User user, Person person) {
        this.user = user;
        this.firstLogin = user.isNewUser();
        this.person = person;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public DateTime getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(DateTime loginDate) {
        this.loginDate = loginDate;
    }
}
