package ee.hm.dop.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;

/**
 * Created by mart on 29.12.15.
 */
@Entity
public class ImproperContent {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "learningObject")
    private LearningObject learningObject;

    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime added;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column
    private String reason;

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
    public DateTime getAdded() {
        return added;
    }

    @JsonDeserialize(using = DateTimeDeserializer.class)
    public void setAdded(DateTime added) {
        this.added = added;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LearningObject getLearningObject() {
        return learningObject;
    }

    public void setLearningObject(LearningObject learningObject) {
        this.learningObject = learningObject;
    }
}
