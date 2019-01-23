package ee.hm.dop.model.taxon;

import static javax.persistence.FetchType.LAZY;

import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

@Entity
@DiscriminatorValue("SPECIALIZATION")
public class Specialization extends Taxon {

    @OneToMany(mappedBy = "specialization")
    @Where(clause = "used = 1")
    private Set<Module> modules;

    @JsonIgnore
    @Column(nullable = false, insertable = false)
    private boolean used;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "domain", nullable = false)
    private Domain domain;

    @JsonIgnore
    @Override
    public String getSolrLevel() {
        return "specialization";
    }

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

    @JsonIgnore
    @Override
    public Set<? extends Taxon> getChildren() {
        return getModules();
    }

    @Override
    public boolean isUsed() {
        return used;
    }

    @Override
    public void setUsed(boolean used) {
        this.used = used;
    }
}
