package ee.hm.dop.model.taxon;

import static javax.persistence.FetchType.LAZY;

import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue("SUBJECT")
public class Subject extends Taxon {

    @OneToMany(mappedBy = "subject")
    private Set<Topic> topics;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "domain", nullable = false)
    private Domain domain;

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
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
