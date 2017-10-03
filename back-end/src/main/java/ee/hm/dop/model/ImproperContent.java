package ee.hm.dop.model;

import javax.persistence.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.enums.ReviewStatus;
import ee.hm.dop.model.enums.ReportingReason;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Created by mart on 29.12.15.
 */
@Entity
public class ImproperContent extends AbstractEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "learningObject")
    private LearningObject learningObject;

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

    @Column
    private String reportingText;

    @Column
    private ReportingReason reportingReason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    @JsonSerialize(using = DateTimeSerializer.class)
    public DateTime getCreatedAt() {
        return createdAt;
    }

    @JsonDeserialize(using = DateTimeDeserializer.class)
    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
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

    public String getReportingText() {
        return reportingText;
    }

    public void setReportingText(String reportingText) {
        this.reportingText = reportingText;
    }

    public LearningObject getLearningObject() {
        return learningObject;
    }

    public void setLearningObject(LearningObject learningObject) {
        this.learningObject = learningObject;
    }

    public ReportingReason getReportingReason() {
        return reportingReason;
    }

    public void setReportingReason(ReportingReason reportingReason) {
        this.reportingReason = reportingReason;
    }
}
