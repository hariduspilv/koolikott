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
@DiscriminatorValue("TOPIC")
public class Topic extends Taxon {

    @OneToMany(mappedBy = "topic")
    private Set<Subtopic> subtopics;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subject")
    private Subject subject;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "domain")
    private Domain domain;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "module")
    private Module module;

    public Set<Subtopic> getSubtopics() {
        return subtopics;
    }

    public void setSubtopics(Set<Subtopic> subtopics) {
        this.subtopics = subtopics;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    @JsonIgnore
    @Override
    public Taxon getParent() {
        if(subject != null) return subject;
        if(domain != null) return  domain;
        if(module != null) return module;
        return null;
    }
}
