package ee.hm.dop.model.taxon;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue("SPECIALIZATION")
public class Specialization extends Taxon {

    @ManyToOne
    @JoinColumn(name = "domain", nullable = false)
    private Domain domain;

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    @JsonIgnore
    @Override
    public Taxon getParent() {
        return getDomain();
    }
}
