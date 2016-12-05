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
@DiscriminatorValue("MODULE")
public class Module extends Taxon {

    @OneToMany(fetch = EAGER, mappedBy = "module")
    private Set<Topic> topics;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "specialization", nullable = false)
    private Specialization specialization;

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

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
