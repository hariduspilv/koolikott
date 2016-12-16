package ee.hm.dop.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Created by mart on 13.12.16.
 */
@Entity
public class ContentRow {
    public ContentRow() {
    }

    public ContentRow(List<LearningObject> learningObjects) {
        this.learningObjects = learningObjects;
    }

    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany(fetch = EAGER)
    @Fetch(FetchMode.SELECT)
    @OrderColumn(name = "materialOrder", nullable = false)
    @JoinTable(
            name = "Row_Material",
            joinColumns = {@JoinColumn(name = "row")},
            inverseJoinColumns = {@JoinColumn(name = "material")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"row", "material", "materialOrder"}))
    private List<LearningObject> learningObjects;

    public List<LearningObject> getLearningObjects() {
        return learningObjects;
    }

    public void setLearningObjects(List<LearningObject> learningObjects) {
        this.learningObjects = learningObjects;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
