package ee.hm.dop.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class AuthenticatedUser {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column
    private boolean firstLogin = false;

    @Column
    private String homeOrganization;

    @Column
    private String mails;

    @Column
    private String affiliations;

    @Column
    private String scopedAffiliations;

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

    public String getHomeOrganization() {
        return homeOrganization;
    }

    public void setHomeOrganization(String homeOrganization) {
        this.homeOrganization = homeOrganization;
    }

    public String getMails() {
        return mails;
    }

    public void setMails(String mails) {
        this.mails = mails;
    }

    public String getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(String affiliations) {
        this.affiliations = affiliations;
    }

    public String getScopedAffiliations() {
        return scopedAffiliations;
    }

    public void setScopedAffiliations(String scopedAffiliations) {
        this.scopedAffiliations = scopedAffiliations;
    }
}
