package ee.hm.dop.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.UniqueConstraint;

@Entity
public class Chapter {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT", name = "textValue")
    private String text;

    @ManyToMany(fetch = EAGER)
    @OrderColumn(name = "materialOrder", nullable = false)
    @JoinTable(
            name = "Chapter_Material",
            joinColumns = { @JoinColumn(name = "chapter") },
            inverseJoinColumns = { @JoinColumn(name = "material") },
            uniqueConstraints = @UniqueConstraint(columnNames = { "chapter", "material", "materialOrder" }))
    private List<Material> materials;

    @OneToMany(fetch = EAGER, cascade = { MERGE, PERSIST })
    @JoinColumn(name = "parentChapter")
    private List<Chapter> subchapters;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return title;
    }

    public List<Chapter> getSubchapters() {
        return subchapters;
    }

    public void setSubchapters(List<Chapter> subchapters) {
        this.subchapters = subchapters;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }
}
