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
@DiscriminatorValue("DOMAIN")
public class Domain extends Taxon {

    @OneToMany(mappedBy = "domain")
    private Set<Subject> subjects;

    @OneToMany(mappedBy = "domain")
    private Set<Topic> topics;

    @OneToMany(mappedBy = "domain")
    private Set<Specialization> specializations;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "educationalContext", nullable = false)
    private EducationalContext educationalContext;

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public EducationalContext getEducationalContext() {
        return educationalContext;
    }

    public void setEducationalContext(EducationalContext educationalContext) {
        this.educationalContext = educationalContext;
    }

    @JsonIgnore
    @Override
    public Taxon getParent() {
        return getEducationalContext();
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public Set<Specialization> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(Set<Specialization> specializations) {
        this.specializations = specializations;
    }
}
