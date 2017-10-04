package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
public class FirstReview extends AbstractEntity{

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "learningObject", nullable = false)
    private LearningObject learningObject;

    @Column(nullable = false)
    private boolean reviewed;

    @ManyToOne
    @JoinColumn(name = "reviewedBy")
    private User reviewedBy;

    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime reviewedAt;

    @Column(nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime createdAt;

    @Override
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

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }
}
