package ee.hm.dop.model.taxon;

import static javax.persistence.FetchType.LAZY;

import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Where;

@Entity
@DiscriminatorValue("DOMAIN")
public class Domain extends Taxon {

    @OneToMany(mappedBy = "domain")
    @Where(clause = "used = 1")
    private Set<Subject> subjects;

    @OneToMany(mappedBy = "domain")
    @Where(clause = "used = 1")
    private Set<Topic> topics;

    @JsonIgnore
    @Column(nullable = false, insertable = false)
    private boolean used;

    @OneToMany(mappedBy = "domain")
    @Where(clause = "used = 1")
    private Set<Specialization> specializations;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "educationalContext", nullable = false)
    private EducationalContext educationalContext;

    @JsonIgnore
    @Override
    public String getSolrLevel() {
        return "domain";
    }

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

    @JsonIgnore
    @Override
    public Set<? extends Taxon> getChildren() {
        Set<Subject> subjects = getSubjects();
        if (CollectionUtils.isNotEmpty(subjects)) return subjects;
        Set<Topic> topics = getTopics();
        if (CollectionUtils.isNotEmpty(topics)) return topics;
        return getSpecializations();
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
