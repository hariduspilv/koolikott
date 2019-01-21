package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ee.hm.dop.model.ehis.Person;
import ee.hm.dop.model.enums.LoginFrom;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

import static javax.persistence.CascadeType.ALL;

@Entity
public class AuthenticatedUser implements AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private LocalDateTime loginDate;

    @JsonIgnore
    @Column(nullable = false)
    private LocalDateTime sessionTime;

    @JsonIgnore
    @Column
    private Integer sessionNumber;

    @Column
    @Enumerated(EnumType.STRING)
    private LoginFrom loginFrom;

    public AuthenticatedUser() {
    }

    public AuthenticatedUser(User user, Person person, LoginFrom loginFrom, LocalDateTime loginDate, LocalDateTime sessionTime) {
        this.user = user;
        this.firstLogin = user.isNewUser();
        this.person = person;
        this.loginDate = LocalDateTime.now();
//        this.loginDate = loginDate;
        this.sessionTime = LocalDateTime.now();
        //todo
//        this.sessionTime = sessionTime;
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

    public LocalDateTime getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(LocalDateTime loginDate) {
        this.loginDate = loginDate;
    }

    public boolean isLoggedOut() {
        return loggedOut;
    }

    public void setLoggedOut(boolean loggedOut) {
        this.loggedOut = loggedOut;
    }

    public LocalDateTime getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(LocalDateTime sessionTime) {
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
