package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.ehis.InstitutionEhis;
import ee.hm.dop.model.enums.UserRole;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import ee.hm.dop.rest.jackson.map.TaxonDeserializer;
import ee.hm.dop.rest.jackson.map.TaxonSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class UserProfile implements AbstractEntity{

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "user")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;


    @Column
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column
    private String customRole;

    @Column
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDateTime lastUpdate;


    @Transient
    @JsonDeserialize(contentUsing = TaxonDeserializer.class)
    @JsonSerialize(contentUsing = TaxonSerializer.class)
    private List<Taxon> taxons;

    @Transient
    private List<InstitutionEhis> institutions;

    @Transient
    private String email;

    @Override
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCustomRole() {
        return customRole;
    }

    public void setCustomRole(String customRole) {
        this.customRole = customRole;
    }
}
