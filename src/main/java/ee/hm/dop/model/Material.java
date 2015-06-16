package ee.hm.dop.model;

import static javax.persistence.FetchType.EAGER;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ee.hm.dop.rest.jackson.map.LanguageDeserializer;
import ee.hm.dop.rest.jackson.map.LanguageSerializer;

@Entity
@NamedQueries({ @NamedQuery(name = "Material.findById", query = "SELECT m FROM Material m WHERE m.id = :id") })
public class Material {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @OneToOne
    @JoinColumn(name = "lang")
    private Language language;

    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "Material_Author",
            joinColumns = { @JoinColumn(name = "material") },
            inverseJoinColumns = { @JoinColumn(name = "author") },
            uniqueConstraints = @UniqueConstraint(columnNames = { "material", "author" }))
    private List<Author> authors;

    @OneToOne
    @JoinColumn(name = "issueDate")
    private IssueDate issueDate;

    @OneToMany(fetch = EAGER)
    @JoinColumn(name = "material")
    private List<LanguageString> descriptions;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String source;

    @OneToMany(fetch = EAGER)
    @JoinColumn(name = "material")
    private List<ResourceType> resourceTypes;

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

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public IssueDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(IssueDate issueDate) {
        this.issueDate = issueDate;
    }

    public List<LanguageString> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<LanguageString> descriptions) {
        this.descriptions = descriptions;
    }

    @JsonSerialize(using = LanguageSerializer.class)
    public Language getLanguage() {
        return language;
    }

    @JsonDeserialize(using = LanguageDeserializer.class)
    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<ResourceType> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(List<ResourceType> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }
}
