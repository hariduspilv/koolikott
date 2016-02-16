package ee.hm.dop.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.NoClass;

import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import ee.hm.dop.rest.jackson.map.PictureDeserializer;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = NoClass.class)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class LearningObject {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "targetGroup")
    @ElementCollection(fetch = EAGER)
    @Fetch(FetchMode.SELECT)
    @CollectionTable(name = "LearningObject_TargetGroup", joinColumns = @JoinColumn(name = "learningObject"))
    private List<TargetGroup> targetGroups;

    @ManyToMany(fetch = EAGER, cascade = { PERSIST, MERGE })
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "LearningObject_CrossCurricularTheme",
            joinColumns = { @JoinColumn(name = "learningObject") },
            inverseJoinColumns = { @JoinColumn(name = "crossCurricularTheme") },
            uniqueConstraints = @UniqueConstraint(columnNames = { "learningObject", "crossCurricularTheme" }))
    private List<CrossCurricularTheme> crossCurricularThemes;

    @ManyToMany(fetch = EAGER, cascade = { PERSIST, MERGE })
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "LearningObject_KeyCompetence",
            joinColumns = { @JoinColumn(name = "learningObject") },
            inverseJoinColumns = { @JoinColumn(name = "keyCompetence") },
            uniqueConstraints = @UniqueConstraint(columnNames = { "learningObject", "keyCompetence" }))
    private List<KeyCompetence> keyCompetences;

    @ManyToMany(fetch = EAGER, cascade = { PERSIST, MERGE })
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "LearningObject_Tag",
            joinColumns = { @JoinColumn(name = "learningObject") },
            inverseJoinColumns = { @JoinColumn(name = "tag") },
            uniqueConstraints = @UniqueConstraint(columnNames = { "learningObject", "tag" }))
    private List<Tag> tags;

    @Basic(fetch = LAZY)
    @Lob
    private byte[] picture;

    @Formula("picture is not null")
    private boolean hasPicture;

    @OneToOne(cascade = { PERSIST, MERGE })
    @JoinColumn(name = "recommendation")
    private Recommendation recommendation;

    @OneToMany(fetch = EAGER, cascade = { MERGE, PERSIST })
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "learningObject")
    @OrderBy("added DESC")
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "creator")
    private User creator;

    @Column
    private boolean deleted;

    // The date when the Learning Object was added to the system
    @Column(nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime added;

    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime updated;

    @Column(nullable = false)
    private Long views = (long) 0;

    @Formula(value = "(SELECT COUNT(*) FROM UserLike ul WHERE ul.learningObject = id AND ul.isLiked = 1)")
    private int likes;

    @Formula(value = "(SELECT COUNT(*) FROM UserLike ul WHERE ul.learningObject = id AND ul.isLiked = 0)")
    private int dislikes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<TargetGroup> getTargetGroups() {
        return targetGroups;
    }

    public void setTargetGroups(List<TargetGroup> targetGroups) {
        this.targetGroups = targetGroups;
    }

    public List<CrossCurricularTheme> getCrossCurricularThemes() {
        return crossCurricularThemes;
    }

    public void setCrossCurricularThemes(List<CrossCurricularTheme> crossCurricularThemes) {
        this.crossCurricularThemes = crossCurricularThemes;
    }

    public List<KeyCompetence> getKeyCompetences() {
        return keyCompetences;
    }

    public void setKeyCompetences(List<KeyCompetence> keyCompetences) {
        this.keyCompetences = keyCompetences;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @JsonIgnore
    public byte[] getPicture() {
        return picture;
    }

    @JsonProperty
    @JsonDeserialize(using = PictureDeserializer.class)
    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public boolean getHasPicture() {
        return hasPicture;
    }

    public void setHasPicture(boolean hasPicture) {
        this.hasPicture = hasPicture;
    }

    public Recommendation getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(Recommendation recommendation) {
        this.recommendation = recommendation;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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

    @JsonSerialize(using = DateTimeSerializer.class)
    public DateTime getAdded() {
        return added;
    }

    public void setAdded(DateTime added) {
        this.added = added;
    }

    public DateTime getUpdated() {
        return updated;
    }

    public void setUpdated(DateTime updated) {
        this.updated = updated;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}
