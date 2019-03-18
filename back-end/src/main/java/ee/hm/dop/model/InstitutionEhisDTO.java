package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ee.hm.dop.model.ehis.InstitutionEhis;
import ee.hm.dop.model.taxon.Taxon;

import java.util.Set;

public class InstitutionEhisDTO extends InstitutionEhis {

    public InstitutionEhisDTO() {
    }

    public InstitutionEhisDTO(Long id, Long ehisId, String name, String area) {
        super(id, ehisId, name, area);
    }

    @Override
    public String getStatus() {
        return super.getStatus();
    }

    @Override
    public void setStatus(String status) {
        super.setStatus(status);
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    @Override
    public Long getEhisId() {
        return super.getEhisId();
    }

    @Override
    public void setEhisId(Long ehisId) {
        super.setEhisId(ehisId);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public void setType(String type) {
        super.setType(type);
    }

    @Override
    public String getArea() {
        return super.getArea();
    }

    @Override
    public void setArea(String area) {
        super.setArea(area);
    }
}
