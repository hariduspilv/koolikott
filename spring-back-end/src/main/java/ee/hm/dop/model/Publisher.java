package ee.hm.dop.model;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Publisher implements AbstractEntity {

    static PolicyFactory NO_ALLOWED_HTML_TAGS = new HtmlPolicyBuilder()
            .toFactory();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true)
    private String website;

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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    private String getSanitizedText(String text) {
        if (text != null) {
            text = NO_ALLOWED_HTML_TAGS.sanitize(text);
        }
        return text;
    }
}
