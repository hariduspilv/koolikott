package ee.hm.dop.model;


import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.List;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

@Entity
@Table(name = "Material")
public class ReducedMaterial extends ReducedLearningObject {

    @ManyToMany(fetch = EAGER, cascade = {PERSIST, MERGE})
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "Material_Title",
            joinColumns = {@JoinColumn(name = "material")},
            inverseJoinColumns = {@JoinColumn(name = "title")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"material", "title"}))
    private List<LanguageString> titles;

    @ManyToMany(fetch = EAGER, cascade = {PERSIST, MERGE})
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "Material_Author",
            joinColumns = {@JoinColumn(name = "material")},
            inverseJoinColumns = {@JoinColumn(name = "author")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"material", "author"}))
    private List<Author> authors;

    @ManyToMany(fetch = EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "Material_ResourceType",
            joinColumns = {@JoinColumn(name = "material")},
            inverseJoinColumns = {@JoinColumn(name = "resourceType")},
            uniqueConstraints = @UniqueConstraint(columnNames = {"material", "resourceType"}))
    private List<ResourceType> resourceTypes;

    public List<LanguageString> getTitles() {
        return titles;
    }

    public void setTitles(List<LanguageString> titles) {
        this.titles = titles;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<ResourceType> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(List<ResourceType> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }
}
