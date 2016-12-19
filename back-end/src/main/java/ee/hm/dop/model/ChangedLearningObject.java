package ee.hm.dop.model;

import ee.hm.dop.model.taxon.Taxon;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ChangedLearningObject {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "learningObject")
    private LearningObject learningObject;

    @ManyToOne
    @JoinColumn(name = "taxon")
    private Taxon taxon;

    @ManyToOne
    @JoinColumn(name = "resourceType")
    private ResourceType resourceType;

    @ManyToOne
    @JoinColumn(name = "targetGroup")
    private TargetGroup targetGroup;

    @ManyToOne
    @JoinColumn(name = "changer")
    private User changer;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LearningObject getLearningObject() {
        return learningObject;
    }

    public void setLearningObject(LearningObject learningObject) {
        this.learningObject = learningObject;
    }

    public Taxon getTaxon() {
        return taxon;
    }

    public void setTaxon(Taxon taxon) {
        this.taxon = taxon;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public TargetGroup getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(TargetGroup targetGroup) {
        this.targetGroup = targetGroup;
    }

    public User getChanger() {
        return changer;
    }

    public void setChanger(User changer) {
        this.changer = changer;
    }
}
