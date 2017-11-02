package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import ee.hm.dop.rest.jackson.map.TaxonDeserializer;
import ee.hm.dop.rest.jackson.map.TaxonSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
public class ReviewableChange implements AbstractEntity {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    private Long learningObject;

    @ManyToOne
    @JoinColumn(name = "taxon")
    @JsonDeserialize(using = TaxonDeserializer.class)
    @JsonSerialize(using = TaxonSerializer.class)
    private Taxon taxon;

    @ManyToOne
    @JoinColumn(name = "resourceType")
    private ResourceType resourceType;

    @ManyToOne
    @JoinColumn(name = "targetGroup")
    private TargetGroup targetGroup;

    @ManyToOne
    @JoinColumn(name = "createdBy")
    private User createdBy;

    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime createdAt;

    @Column(nullable = false)
    private boolean reviewed = false;

    @ManyToOne
    @JoinColumn(name = "reviewedBy")
    private User reviewedBy;

    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime reviewedAt;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status;


    @Column(columnDefinition = "TEXT")
    private String materialSource;

    @JsonIgnore
    public boolean hasChange() {
        return this.getTaxon() != null || this.getResourceType() != null || this.getTargetGroup() != null || this.getMaterialSource() != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLearningObject() {
        return learningObject;
    }

    public void setLearningObject(Long learningObject) {
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public DateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(DateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }

    public String getMaterialSource() {
        return materialSource;
    }

    public void setMaterialSource(String materialSource) {
        this.materialSource = materialSource;
    }

}
