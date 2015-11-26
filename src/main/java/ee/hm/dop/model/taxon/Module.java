package ee.hm.dop.model.taxon;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue("MODULE")
public class Module extends Taxon {

    @ManyToOne
    @JoinColumn(name = "specialization", nullable = false)
    private Specialization specialization;

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    @JsonIgnore
    @Override
    public Taxon getParent() {
        return getSpecialization();
    }
}
