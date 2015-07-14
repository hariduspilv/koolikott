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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import ee.hm.dop.rest.jackson.map.LanguageDeserializer;
import ee.hm.dop.rest.jackson.map.LanguageSerializer;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Material.class)
@NamedQueries({ @NamedQuery(name = "Material.findById", query = "SELECT m FROM Material m WHERE m.id = :id"),
        @NamedQuery(name = "Material.findAllById", query = "SELECT m FROM Material m WHERE m.id in :idList") })
public class Material {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "Material_Title",
            joinColumns = { @JoinColumn(name = "material") },
            inverseJoinColumns = { @JoinColumn(name = "title") },
            uniqueConstraints = @UniqueConstraint(columnNames = { "material", "title" }))
    private List<LanguageString> titles;

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

    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "Material_Description",
            joinColumns = { @JoinColumn(name = "material") },
            inverseJoinColumns = { @JoinColumn(name = "description") },
            uniqueConstraints = @UniqueConstraint(columnNames = { "material", "description" }))
    private List<LanguageString> descriptions;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String source;

    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "Material_ResourceType",
            joinColumns = { @JoinColumn(name = "material") },
            inverseJoinColumns = { @JoinColumn(name = "resourceType") },
            uniqueConstraints = @UniqueConstraint(columnNames = { "material", "resourceType" }))
    private List<ResourceType> resourceTypes;

    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "Material_Classification",
            joinColumns = { @JoinColumn(name = "material") },
            inverseJoinColumns = { @JoinColumn(name = "classification") },
            uniqueConstraints = @UniqueConstraint(columnNames = { "material", "classification" }))
    private List<Classification> classifications;

    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "Material_EducationalContext",
            joinColumns = { @JoinColumn(name = "material") },
            inverseJoinColumns = { @JoinColumn(name = "educationalContext") },
            uniqueConstraints = @UniqueConstraint(columnNames = { "material", "educationalContext" }))
    private List<EducationalContext> educationalContexts;

    @ManyToOne
    @JoinColumn(name = "licenseType")
    private LicenseType licenseType;

    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "Material_Publisher",
            joinColumns = { @JoinColumn(name = "material") },
            inverseJoinColumns = { @JoinColumn(name = "publisher") },
            uniqueConstraints = @UniqueConstraint(columnNames = { "material", "publisher" }))
    private List<Publisher> publishers;

    @Column(nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime added;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime updated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<Classification> getClassifications() {
        return classifications;
    }

    public void setClassifications(List<Classification> classifications) {
        this.classifications = classifications;
    }

    public List<EducationalContext> getEducationalContexts() {
        return educationalContexts;
    }

    public void setEducationalContexts(List<EducationalContext> educationalContexts) {
        this.educationalContexts = educationalContexts;
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public List<Publisher> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<Publisher> publishers) {
        this.publishers = publishers;
    }

    @JsonSerialize(using = DateTimeSerializer.class)
    public DateTime getAdded() {
        return added;
    }

    @JsonDeserialize(using = DateTimeDeserializer.class)
    public void setAdded(DateTime added) {
        this.added = added;
    }

    @JsonSerialize(using = DateTimeSerializer.class)
    public DateTime getUpdated() {
        return updated;
    }

    @JsonDeserialize(using = DateTimeDeserializer.class)
    public void setUpdated(DateTime updated) {
        this.updated = updated;
    }

}
