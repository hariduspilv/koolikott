package ee.hm.dop.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * Created by mart.laus on 17.06.2015.
 */

@Entity
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id", scope = Classification.class) wont work with getAll somehow
public class Classification {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "classificationName", nullable = false)
    private String name;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Classification> children;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "parent")
    private Classification parent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Classification> getChildren() {
        return children;
    }

    public void setChildren(List<Classification> children) {
        this.children = children;
    }

    public Classification getParent() {
        return parent;
    }

    public void setParent(Classification parent) {
        this.parent = parent;
    }
}
