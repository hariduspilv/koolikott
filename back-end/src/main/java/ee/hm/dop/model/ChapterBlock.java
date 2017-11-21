package ee.hm.dop.model;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ChapterBlock {

    static PolicyFactory BLOCK_ALLOWED_HTML_TAGS_POLICY = new HtmlPolicyBuilder()
            .allowStandardUrlProtocols()
            .allowElements("h3", "p", "ul", "li", "blockquote", "a", "b", "i", "div", "br")
            .allowAttributes("href", "target", "class", "id")
            .onElements("a")
            .allowAttributes("class", "id", "data-id", "data-src")
            .onElements("div")
            .allowAttributes("class", "id")
            .onElements("h3", "p", "ul", "li", "blockquote", "b", "i", "br")
            .toFactory();

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private boolean narrow;

    @Column(columnDefinition = "TEXT")
    private String htmlContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isNarrow() {
        return narrow;
    }

    public void setNarrow(boolean narrow) {
        this.narrow = narrow;
    }

    public String getHtmlContent() {
        if (htmlContent != null) {
            htmlContent = BLOCK_ALLOWED_HTML_TAGS_POLICY.sanitize(htmlContent);
        }
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
}
