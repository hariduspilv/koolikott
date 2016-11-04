package ee.hm.dop.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.rest.jackson.map.RoleSerializer;

import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @JsonIgnore
    @Column(unique = true, nullable = false)
    private String idCode;

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
    private List<Taxon> userTaxons;

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
}
