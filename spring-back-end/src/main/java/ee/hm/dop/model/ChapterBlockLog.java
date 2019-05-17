package ee.hm.dop.model;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import javax.persistence.*;

@Entity
@Table(name = "ChapterBlock_Log")
public class ChapterBlockLog {

    static PolicyFactory BLOCK_ALLOWED_HTML_TAGS_POLICY = new HtmlPolicyBuilder()
            .allowStandardUrlProtocols() //keep orders alphabetical
            .allowElements("a", "b", "blockquote", "br", "div", "em", "i", "h3", "li", "p", "strong", "ul")
            .allowAttributes("class", "href", "id", "target")
            .onElements("a")
            .allowAttributes("class", "data-id", "data-src", "id")
            .onElements("div")
            .allowAttributes("class", "id")
            .onElements("b", "blockquote", "br", "em", "h3", "i", "li", "p", "strong", "ul")
            .toFactory();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
        if (htmlContent != null) {
            htmlContent = BLOCK_ALLOWED_HTML_TAGS_POLICY.sanitize(htmlContent);
        }
        this.htmlContent = htmlContent;
    }
}
