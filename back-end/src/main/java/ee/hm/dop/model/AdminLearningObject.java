package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.NoClass;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.model.interfaces.ILearningObject;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = NoClass.class)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "LearningObject")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public abstract class AdminLearningObject implements Searchable, ILearningObject, AbstractEntity {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator")
    private User creator;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @Column(nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime added;

    @OneToMany(mappedBy = "learningObject", fetch = LAZY)
    private List<ReviewableChange> reviewableChanges;

    @OneToMany(mappedBy = "learningObject", fetch = LAZY)
    private List<FirstReview> firstReviews;

    @OneToMany(mappedBy = "learningObject", fetch = LAZY)
    private List<ImproperContent> improperContents;

    @Column(nullable = false)
    private boolean deleted = false;

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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<ReviewableChange> getReviewableChanges() {
        return reviewableChanges;
    }

    public void setReviewableChanges(List<ReviewableChange> reviewableChanges) {
        this.reviewableChanges = reviewableChanges;
    }

    public List<FirstReview> getFirstReviews() {
        return firstReviews;
    }

    public void setFirstReviews(List<FirstReview> firstReviews) {
        this.firstReviews = firstReviews;
    }

    public DateTime getAdded() {
        return added;
    }

    public void setAdded(DateTime added) {
        this.added = added;
    }

    public List<ImproperContent> getImproperContents() {
        return improperContents;
    }

    public void setImproperContents(List<ImproperContent> improperContents) {
        this.improperContents = improperContents;
    }
}
