package ee.hm.dop.model;

import ee.hm.dop.model.interfaces.IMaterial;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "Material")
public class AdminMaterial extends AdminLearningObject implements IMaterial {

    @ManyToMany(fetch = EAGER, cascade = {PERSIST, MERGE})
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "Material_Title",
            joinColumns = {@JoinColumn(name = "material")},
            inverseJoinColumns = {@JoinColumn(name = "title")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"material", "title"}))
    private List<LanguageString> titles;

    @ManyToMany(fetch = EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "Material_ResourceType",
            joinColumns = {@JoinColumn(name = "material")},
            inverseJoinColumns = {@JoinColumn(name = "resourceType")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"material", "resourceType"}))
    private List<ResourceType> resourceTypes;

//    @OneToMany(mappedBy = "material", fetch = LAZY)
//    private List<BrokenContent> brokenContents;

    public List<LanguageString> getTitles() {
        return titles;
    }

    public void setTitles(List<LanguageString> titles) {
        this.titles = titles;
    }

    public List<ResourceType> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(List<ResourceType> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }
}
