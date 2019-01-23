package ee.hm.dop.model;

import static javax.persistence.CascadeType.ALL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ee.hm.dop.model.ehis.Person;
import ee.hm.dop.model.enums.LoginFrom;
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

    @JsonIgnore
    @Column
    private boolean deleted;

    @JsonIgnore
    @Column
    private boolean loggedOut;

    @JsonIgnore
    @Column
    private boolean declined;

    @OneToOne(cascade = ALL)
    @JoinColumn(name = "person")
    private Person person;

    @JsonIgnore
    @Column(nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime loginDate;

    @JsonIgnore
    @Column(nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime sessionTime;

    @JsonIgnore
    @Column
    private Integer sessionNumber;

    @Column
    @Enumerated(EnumType.STRING)
    private LoginFrom loginFrom;

    public AuthenticatedUser() {
    }

    public AuthenticatedUser(User user, Person person, LoginFrom loginFrom, DateTime loginDate, DateTime sessionTime) {
        this.user = user;
        this.firstLogin = user.isNewUser();
        this.person = person;
        this.loginDate = loginDate;
        this.sessionTime = sessionTime;
        this.sessionNumber = 1;
        this.loginFrom = loginFrom;
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

    public boolean isLoggedOut() {
        return loggedOut;
    }

    public void setLoggedOut(boolean loggedOut) {
        this.loggedOut = loggedOut;
    }

    public DateTime getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(DateTime sessionTime) {
        this.sessionTime = sessionTime;
    }

    public Integer getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(Integer sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public boolean isDeclined() {
        return declined;
    }

    public void setDeclined(boolean declined) {
        this.declined = declined;
    }

    public LoginFrom getLoginFrom() {
        return loginFrom;
    }

    public void setLoginFrom(LoginFrom loginFrom) {
        this.loginFrom = loginFrom;
    }
}
