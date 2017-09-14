package ee.hm.dop.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.rest.jackson.map.TaxonDeserializer;
import ee.hm.dop.rest.jackson.map.TaxonSerializer;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.util.List;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "LearningObject")
public abstract class ReducedLearningObject extends AbstractEntity implements Searchable {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator")
    private User creator;

    @ManyToOne
    @JoinColumn(name = "picture")
    private OriginalPicture picture;

    @ManyToMany(fetch = EAGER, cascade = {MERGE, PERSIST})
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "LearningObject_TargetGroup",
            joinColumns = {@JoinColumn(name = "learningObject")},
            inverseJoinColumns = {@JoinColumn(name = "targetGroup")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"learningObject", "targetGroup"}))
    private List<TargetGroup> targetGroups;

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

    @Formula(value = "(SELECT COUNT(*) FROM UserLike ul WHERE ul.learningObject = id AND ul.isLiked = 1)")
    private int likes;

    @Formula(value = "(SELECT COUNT(*) FROM UserLike ul WHERE ul.learningObject = id AND ul.isLiked = 0)")
    private int dislikes;

    @Formula(value = "(SELECT COUNT(*) FROM Comment c WHERE c.learningObject = id)")
    private int commentCount;

    private Long views;

    private boolean deleted = false;

    @Transient
    private Boolean favorite;

    public OriginalPicture getPicture() {
        return picture;
    }

    public void setPicture(OriginalPicture picture) {
        this.picture = picture;
    }

    public List<TargetGroup> getTargetGroups() {
        return targetGroups;
    }

    public void setTargetGroups(List<TargetGroup> targetGroups) {
        this.targetGroups = targetGroups;
    }

    public List<Taxon> getTaxons() {
        return taxons;
    }

    public void setTaxons(List<Taxon> taxons) {
        this.taxons = taxons;
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

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

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

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
}
