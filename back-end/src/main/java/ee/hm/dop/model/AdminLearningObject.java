package ee.hm.dop.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.model.interfaces.ILearningObject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.rest.jackson.map.TaxonDeserializer;
import ee.hm.dop.rest.jackson.map.TaxonSerializer;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "LearningObject")
public abstract class AdminLearningObject implements Searchable, ILearningObject, AbstractEntity {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator")
    private User creator;

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
    private List<ReviewableChange> reviewableChanges;

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

    public List<Taxon> getTaxons() {
        return taxons;
    }

    public void setTaxons(List<Taxon> taxons) {
        this.taxons = taxons;
    }
}
