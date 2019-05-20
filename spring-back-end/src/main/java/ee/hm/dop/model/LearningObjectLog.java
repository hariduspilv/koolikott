package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.NoClass;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.model.interfaces.ILearningObject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import ee.hm.dop.rest.jackson.map.TaxonDeserializer;
import ee.hm.dop.rest.jackson.map.TaxonSerializer;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = NoClass.class)
@Entity
@Table(name = "LearningObject_Log")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class LearningObjectLog implements Searchable, ILearningObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long learningObject;

    @Column
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @ManyToMany(fetch = EAGER, cascade = {MERGE, PERSIST})
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "LearningObject_TargetGroup_Log",
            joinColumns = {@JoinColumn(name = "learningObject")},
            inverseJoinColumns = {@JoinColumn(name = "targetGroup")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"learningObject", "targetGroup"}))
    private List<TargetGroup> targetGroups;

    @ManyToMany(fetch = EAGER, cascade = {PERSIST, MERGE})
    @Fetch(FetchMode.SELECT)

    @JoinTable(
            name = "LearningObject_CrossCurricularTheme_Log",
            joinColumns = {@JoinColumn(name = "learningObject")},
            inverseJoinColumns = {@JoinColumn(name = "crossCurricularTheme")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"learningObject", "crossCurricularTheme"}))
    private List<CrossCurricularTheme> crossCurricularThemes;

    @ManyToMany(fetch = EAGER, cascade = {PERSIST, MERGE})
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "LearningObject_KeyCompetence_Log",
            joinColumns = {@JoinColumn(name = "learningObject")},
            inverseJoinColumns = {@JoinColumn(name = "keyCompetence")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"learningObject", "keyCompetence"}))
    private List<KeyCompetence> keyCompetences;

    @ManyToMany(fetch = EAGER, cascade = {PERSIST, MERGE})
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "LearningObject_Tag_Log",
            joinColumns = {@JoinColumn(name = "learningObject")},
            inverseJoinColumns = {@JoinColumn(name = "tag")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"learningObject", "tag"}))
    private List<Tag> tags;

    @ManyToOne(fetch = EAGER, cascade = {MERGE, PERSIST})
    @JoinColumn(name = "picture")
    private OriginalPicture picture;

    @OneToOne(cascade = {PERSIST, MERGE})
    @JoinColumn(name = "recommendation")
    private Recommendation recommendation;

    @ManyToOne
    @JoinColumn(name = "creator")
    private User creator;

    @Column(nullable = false)
    private boolean deleted;

    // The date when the Learning Object was added to the system
    @Column(nullable = false)
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDateTime added;

    @Column
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDateTime updated;

    @Column(nullable = false)
    private Long views = (long) 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @ManyToMany(fetch = EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "LearningObject_Taxon_Log",
            joinColumns = {@JoinColumn(name = "learningObject")},
            inverseJoinColumns = {@JoinColumn(name = "taxon")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"learningObject", "taxon"}))
    @JsonDeserialize(contentUsing = TaxonDeserializer.class)
    @JsonSerialize(contentUsing = TaxonSerializer.class)
    private List<Taxon> taxons;

    /**
     * Last time when something was done to this LearningObject. It includes
     * tagging, up-voting, recommending and so on
     */
    @Column
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDateTime lastInteraction;

    @Column(nullable = false)
    private boolean publicationConfirmed;

    @ManyToOne
    @JoinColumn(name = "licenseType")
    private LicenseType licenseType;

    @Column
    private Boolean isAutoSaved;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
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

    public OriginalPicture getPicture() {
        return picture;
    }

    public void setPicture(OriginalPicture picture) {
        this.picture = picture;
    }

    public Recommendation getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(Recommendation recommendation) {
        this.recommendation = recommendation;
    }

    @Override
    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getAdded() {
        return added;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public LocalDateTime getLastInteraction() {
        return lastInteraction;
    }

    public void setLastInteraction(LocalDateTime lastInteraction) {
        this.lastInteraction = lastInteraction;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    @Override
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<Taxon> getTaxons() {
        return taxons;
    }

    public void setTaxons(List<Taxon> taxons) {
        this.taxons = taxons;
    }

    public void setAdded(LocalDateTime added) {
        this.added = added;
    }

    public boolean isPublicationConfirmed() {
        return publicationConfirmed;
    }

    public void setPublicationConfirmed(boolean publicationConfirmed) {
        this.publicationConfirmed = publicationConfirmed;
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public Boolean getAutoSaved() {
        return isAutoSaved;
    }

    public void setAutoSaved(Boolean autoSaved) {
        isAutoSaved = autoSaved;
    }
}
