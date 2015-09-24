package ee.hm.dop.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Chapter {

    @Id
    private Long id;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT", name = "textValue")
    private String text;

    @ManyToOne
    @JoinColumn(name = "material")
    private Material material;

    @OneToOne
    @JoinColumn(name = "subchapter")
    private Chapter subchapter;

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

    public Chapter getSubchapter() {
        return subchapter;
    }

    public void setSubchapter(Chapter subchapter) {
        this.subchapter = subchapter;
    }

    @Override
    public String toString() {
        return title;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
