package ee.hm.dop.model.taxon;

import static javax.persistence.FetchType.EAGER;

import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue("SPECIALIZATION")
public class Specialization extends Taxon {

    @OneToMany(fetch = EAGER, mappedBy = "specialization")
    private Set<Module> modules;

    @ManyToOne
    @JoinColumn(name = "domain", nullable = false)
    private Domain domain;

    public Set<Module> getModules() {
        return modules;
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
    }

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
