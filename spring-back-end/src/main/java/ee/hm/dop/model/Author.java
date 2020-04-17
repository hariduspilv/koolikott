package ee.hm.dop.model;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import javax.persistence.*;

@Entity
@Cacheable
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "surname"}))
public class Author implements AbstractEntity {

    static PolicyFactory NO_ALLOWED_HTML_TAGS = new HtmlPolicyBuilder()
            .toFactory();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

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
        this.name = getSanitizedText(name);
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = getSanitizedText(surname);
    }

    private String getSanitizedText(String text) {
        if (text != null) {
            text = NO_ALLOWED_HTML_TAGS.sanitize(text);
        }
        return text;
    }

}
