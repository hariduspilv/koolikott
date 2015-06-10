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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Material {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "Material_Author",
            joinColumns = { @JoinColumn(name = "material") },
            inverseJoinColumns = { @JoinColumn(name = "author") })
    private List<Author> authors;

    @OneToOne
    @JoinColumn(name = "issueDate")
    private IssueDate issueDate;

    @OneToMany(fetch = EAGER)
    @JoinColumn(name = "material")
    private List<LanguageDescription> descriptions;

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

    public List<LanguageDescription> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<LanguageDescription> descriptions) {
        this.descriptions = descriptions;
    }
}
