package ee.hm.dop.model.ehis;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Institution {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @Column
    private String ehisId;

    @ElementCollection
    @CollectionTable(name = "Institution_Roles", joinColumns = { @JoinColumn(name = "institution") })
    private List<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEhisId() {
        return ehisId;
    }

    public void setEhisId(String id) {
        this.ehisId = id;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
