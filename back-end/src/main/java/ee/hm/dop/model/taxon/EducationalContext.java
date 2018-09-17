package ee.hm.dop.model.taxon;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

@Entity
@DiscriminatorValue("EDUCATIONAL_CONTEXT")
public class EducationalContext extends Taxon {

    @OneToMany(mappedBy = "educationalContext")
    @Where(clause = "used = 1")
    private Set<Domain> domains;

    @JsonIgnore
    @Column(nullable = false, insertable = false)
    private boolean used;

    @JsonIgnore
    @Override
    public String getSolrLevel() {
        return "educational_context";
    }

    public Set<Domain> getDomains() {
        return domains;
    }

    public void setDomains(Set<Domain> domains) {
        this.domains = domains;
    }

    @JsonIgnore
    @Override
    public Taxon getParent() {
        return null;
    }

    @JsonIgnore
    @Override
    public Set<? extends Taxon> getChildren() {
        return getDomains();
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
