package ee.hm.dop.model.taxon;

import static javax.persistence.FetchType.LAZY;

import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

@Entity
@DiscriminatorValue("SUBJECT")
public class Subject extends Taxon {

    @OneToMany(mappedBy = "subject")
    @Where(clause = "used = 'true'")
    private Set<Topic> topics;

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
        return "subject";
    }

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

    @JsonIgnore
    @Override
    public Set<? extends Taxon> getChildren() {
        return getTopics();
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
