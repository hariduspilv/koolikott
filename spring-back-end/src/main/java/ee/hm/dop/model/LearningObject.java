package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.NoClass;
import ee.hm.dop.model.enums.SaveType;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.model.interfaces.ILearningObject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import ee.hm.dop.rest.jackson.map.TaxonDeserializer;
import ee.hm.dop.rest.jackson.map.TaxonSerializer;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = NoClass.class)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class LearningObject implements Searchable, ILearningObject {

    static PolicyFactory LO_ALLOWED_HTML_TAGS_POLICY = new HtmlPolicyBuilder().allowStandardUrlProtocols()
            .allowElements("a", "b", "blockquote", "br", "div", "i", "li", "ol", "p", "pre", "ul")
            .allowAttributes("href", "target")
            .onElements("a")
            .toFactory();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = EAGER, cascade = {MERGE, PERSIST})
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "LearningObject_TargetGroup",
            joinColumns = {@JoinColumn(name = "learningObject")},
            inverseJoinColumns = {@JoinColumn(name = "targetGroup")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"learningObject", "targetGroup"}))
    private List<TargetGroup> targetGroups;

    @ManyToMany(fetch = EAGER, cascade = {PERSIST, MERGE})
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "LearningObject_CrossCurricularTheme",
            joinColumns = {@JoinColumn(name = "learningObject")},
            inverseJoinColumns = {@JoinColumn(name = "crossCurricularTheme")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"learningObject", "crossCurricularTheme"}))
    private List<CrossCurricularTheme> crossCurricularThemes;

    @ManyToMany(fetch = EAGER, cascade = {PERSIST, MERGE})
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "LearningObject_KeyCompetence",
            joinColumns = {@JoinColumn(name = "learningObject")},
            inverseJoinColumns = {@JoinColumn(name = "keyCompetence")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"learningObject", "keyCompetence"}))
    private List<KeyCompetence> keyCompetences;

    @ManyToMany(fetch = EAGER, cascade = {PERSIST, MERGE})
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "LearningObject_Tag",
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

    @OneToMany(fetch = EAGER, cascade = {MERGE, PERSIST})
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "learningObject")
    @OrderBy("added DESC")
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "creator")
    private User creator;

    @Column(nullable = false)
    private boolean deleted = false;

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
            name = "LearningObject_Taxon",
            joinColumns = {@JoinColumn(name = "learningObject")},
            inverseJoinColumns = {@JoinColumn(name = "taxon")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"learningObject", "taxon"}))
    @JsonDeserialize(contentUsing = TaxonDeserializer.class)
    @JsonSerialize(contentUsing = TaxonSerializer.class)
    private List<Taxon> taxons;

    @OneToMany(mappedBy = "learningObject", fetch = LAZY)
    @JsonBackReference("firstReview")
    private List<FirstReview> firstReviews;

    @OneToMany(mappedBy = "learningObject", fetch = LAZY)
    @JsonBackReference("improperContent")
    private List<ImproperContent> improperContents;

    @OneToMany(mappedBy = "learningObject", fetch = LAZY)
    @JsonBackReference("reviewableChange")
    private List<ReviewableChange> reviewableChanges;

    @Formula(value = "(SELECT COUNT(*) FROM UserLike ul WHERE ul.learningObject = id AND ul.isLiked = 1)")
    private int likes;

    @Formula(value = "(SELECT COUNT(*) FROM UserLike ul WHERE ul.learningObject = id AND ul.isLiked = 0)")
    private int dislikes;

    @Formula(value = "(SELECT COUNT(*) FROM ImproperContent ic WHERE ic.learningObject = id AND ic.reviewed = 0)")
    private int improper;

    @Formula(value = "(SELECT COUNT(*) FROM ReviewableChange rc WHERE rc.learningObject = id and rc.reviewed = 0)")
    private int changed;

    @Formula(value = "(SELECT COUNT(*) FROM FirstReview fr WHERE fr.learningObject = id AND fr.reviewed = 0)")
    private int unReviewed;

    /**
     * Last time when something was done to this LearningObject. It includes
     * tagging, up-voting, recommending and so on
     */
    @JsonIgnore
    @Column
    private LocalDateTime lastInteraction;

    @Column(nullable = false)
    private boolean publicationConfirmed = false;

    @ManyToOne
    @JoinColumn(name = "licenseType")
    private LicenseType licenseType;

    @Transient
    private Boolean favorite;

    @Transient
    private String educationalContext;

    @Transient
    private String domain;

    @Transient
    @Enumerated(EnumType.STRING)
    private SaveType saveType;

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

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = (OriginalPicture) picture;
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

    public LocalDateTime getAdded() {
        return added;
    }

    public void setAdded(LocalDateTime added) {
        this.added = added;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
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

    public LocalDateTime getLastInteraction() {
        return lastInteraction;
    }

    public void setLastInteraction(LocalDateTime lastInteraction) {
        this.lastInteraction = lastInteraction;
    }

    public int getImproper() {
        return improper;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public int getChanged() {
        return changed;
    }

    public void setChanged(int changed) {
        this.changed = changed;
    }

    public List<Taxon> getTaxons() {
        return taxons;
    }

    public void setTaxons(List<Taxon> taxons) {
        this.taxons = taxons;
    }

    public int getUnReviewed() {
        return unReviewed;
    }

    public void setUnReviewed(int unReviewed) {
        this.unReviewed = unReviewed;
    }

    public void setImproper(int improper) {
        this.improper = improper;
    }

    public List<FirstReview> getFirstReviews() {
        return firstReviews;
    }

    public void setFirstReviews(List<FirstReview> firstReviews) {
        this.firstReviews = firstReviews;
    }

    public List<ImproperContent> getImproperContents() {
        return improperContents;
    }

    public void setImproperContents(List<ImproperContent> improperContents) {
        this.improperContents = improperContents;
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

    public SaveType getSaveType() {
        return saveType;
    }

    public void setSaveType(SaveType saveType) {
        this.saveType = saveType;
    }

    public String getEducationalContext() {
        return educationalContext;
    }

    public void setEducationalContext(String educationalContext) {
        this.educationalContext = educationalContext;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

}
