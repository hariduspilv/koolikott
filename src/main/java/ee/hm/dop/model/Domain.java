package ee.hm.dop.model;

import static javax.persistence.FetchType.EAGER;

import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("DOMAIN")
public class Domain extends Taxon {

    @OneToMany(fetch = EAGER, mappedBy = "domain")
    private Set<Subject> subjects;

    @ManyToOne
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
}
