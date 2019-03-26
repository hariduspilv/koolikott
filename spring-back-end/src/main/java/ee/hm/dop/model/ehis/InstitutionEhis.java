package ee.hm.dop.model.ehis;

import ee.hm.dop.model.AbstractEntity;

import javax.persistence.*;

@Entity
public class InstitutionEhis implements AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ehisId;

    @Column(nullable = false)
    private String name;

    @Column
    private String area;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Transient
    private String status;

    @Transient
    private String type;

    public InstitutionEhis() {
    }

    public InstitutionEhis(Long ehisId, String name, String area, String status, String type) {
        this.ehisId = ehisId;
        this.name = name;
        this.area = area;
        this.status = status;
        this.type = type;
    }

    public InstitutionEhis(Long id, Long ehisId, String name, String area) {
        this.id = id;
        this.ehisId = ehisId;
        this.name = name;
        this.area = area;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

