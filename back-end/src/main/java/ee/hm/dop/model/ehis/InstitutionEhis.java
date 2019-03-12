package ee.hm.dop.model.ehis;

import ee.hm.dop.model.AbstractEntity;

import javax.persistence.*;

@Entity
public class InstitutionEhis implements AbstractEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long ehisId;

    @Column(nullable = false)
    private String name;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Transient
    private String status;

    public InstitutionEhis() {
    }

    public InstitutionEhis(Long ehisId, String name) {
        this.ehisId = ehisId;
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getEhisId() {
        return ehisId;
    }

    public void setEhisId(Long ehisId) {
        this.ehisId = ehisId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

