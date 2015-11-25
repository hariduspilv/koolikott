package ee.hm.dop.model.taxon;

import static javax.persistence.FetchType.EAGER;

import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue("EDUCATIONAL_CONTEXT")
public class EducationalContext extends Taxon {

    @OneToMany(fetch = EAGER, mappedBy = "educationalContext")
    private Set<Domain> domains;

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
}
