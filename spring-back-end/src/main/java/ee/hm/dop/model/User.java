package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.ehis.InstitutionEhis;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.rest.jackson.map.RoleSerializer;
import ee.hm.dop.rest.jackson.map.TaxonDeserializer;
import ee.hm.dop.rest.jackson.map.TaxonSerializer;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements AbstractEntity {

    @JsonView(Views.publicUser.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(Views.publicUser.class)
    @Column(unique = true, nullable = false)
    private String username;

    @JsonView(Views.publicUser.class)
    @Column(nullable = false)
    private String name;

    @JsonView(Views.publicUser.class)
    @Column(nullable = false)
    private String surname;

    @JsonIgnore
    @Column(unique = true, nullable = false)
    private String idCode;

    @Column(columnDefinition = "TEXT")
    private String location;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "publisher")
    private Publisher publisher;

    @OneToMany
    @JoinTable(
            name = "User_Taxon",
            joinColumns = {@JoinColumn(name = "user")},
            inverseJoinColumns = {@JoinColumn(name = "taxon")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"user", "taxon"}))
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonDeserialize(contentUsing = TaxonDeserializer.class)
    @JsonSerialize(contentUsing = TaxonSerializer.class)
    private List<Taxon> userTaxons;

    @OneToMany(cascade = ALL)
    @JoinTable(
            name = "User_InterestTaxon",
            joinColumns = {@JoinColumn(name = "user")},
            inverseJoinColumns = {@JoinColumn(name = "taxon")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"user", "taxon"}))
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonDeserialize(contentUsing = TaxonDeserializer.class)
    @JsonSerialize(contentUsing = TaxonSerializer.class)
    private List<Taxon> taxons;

    @OneToMany
    @JoinTable(
            name = "User_Institution",
            joinColumns = {@JoinColumn(name = "user")},
            inverseJoinColumns = {@JoinColumn(name = "institution")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"user", "institution"}))
    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<InstitutionEhis> institutions;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = LAZY)
    private List<User_Agreement> userAgreements;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = LAZY)
    private List<UserLicenceAgreement> userLicenceAgreements;

    @OneToOne(mappedBy = "user")
    private UserEmail userEmail;

    @OneToOne(mappedBy = "user")
    private UserProfile userProfile;

    @Transient
    @JsonIgnore
    private boolean newUser;

    @JsonIgnore
    public String getFullName(){
        return name + " " + surname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty
    @JsonSerialize(using = RoleSerializer.class)
    public Role getRole() {
        return role;
    }

    @JsonIgnore
    public void setRole(Role role) {
        this.role = role;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public List<Taxon> getUserTaxons() {
        return userTaxons;
    }

    public void setUserTaxons(List<Taxon> userTaxons) {
        this.userTaxons = userTaxons;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public List<User_Agreement> getUserAgreements() {
        return userAgreements;
    }

    public void setUserAgreements(List<User_Agreement> userAgreements) {
        this.userAgreements = userAgreements;
    }

    public List<UserLicenceAgreement> getUserLicenceAgreements() {
        return userLicenceAgreements;
    }

    public void setUserLicenceAgreements(List<UserLicenceAgreement> userLicenceAgreements) {
        this.userLicenceAgreements = userLicenceAgreements;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Taxon> getTaxons() {
        return taxons;
    }

    public void setTaxons(List<Taxon> taxons) {
        this.taxons = taxons;
    }

    public List<InstitutionEhis> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<InstitutionEhis> institutions) {
        this.institutions = institutions;
    }

    public UserEmail getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(UserEmail userEmail) {
        this.userEmail = userEmail;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
