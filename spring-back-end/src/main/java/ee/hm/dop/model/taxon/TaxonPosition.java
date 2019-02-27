package ee.hm.dop.model.taxon;

import ee.hm.dop.model.AbstractEntity;

import javax.persistence.*;

@Entity
public class TaxonPosition implements AbstractEntity {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "taxon")
    private Taxon taxon;

    @OneToOne
    @JoinColumn(name = "educationalContext")
    private Taxon educationalContext;

    @OneToOne
    @JoinColumn(name = "domain")
    private Taxon domain;

    @OneToOne
    @JoinColumn(name = "subject")
    private Taxon subject;

    @OneToOne
    @JoinColumn(name = "module")
    private Taxon module;

    @OneToOne
    @JoinColumn(name = "specialization")
    private Taxon specialization;

    @OneToOne
    @JoinColumn(name = "topic")
    private Taxon topic;

    @OneToOne
    @JoinColumn(name = "subtopic")
    private Taxon subtopic;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Taxon getTaxon() {
        return taxon;
    }

    public void setTaxon(Taxon taxon) {
        this.taxon = taxon;
    }

    public Taxon getEducationalContext() {
        return educationalContext;
    }

    public void setEducationalContext(Taxon educationalContext) {
        this.educationalContext = educationalContext;
    }

    public Taxon getDomain() {
        return domain;
    }

    public void setDomain(Taxon domain) {
        this.domain = domain;
    }

    public Taxon getSubject() {
        return subject;
    }

    public void setSubject(Taxon subject) {
        this.subject = subject;
    }

    public Taxon getModule() {
        return module;
    }

    public void setModule(Taxon module) {
        this.module = module;
    }

    public Taxon getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Taxon specialization) {
        this.specialization = specialization;
    }

    public Taxon getTopic() {
        return topic;
    }

    public void setTopic(Taxon topic) {
        this.topic = topic;
    }

    public Taxon getSubtopic() {
        return subtopic;
    }

    public void setSubtopic(Taxon subtopic) {
        this.subtopic = subtopic;
    }
}
