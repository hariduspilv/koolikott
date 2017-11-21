package ee.hm.dop.model;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ChapterBlock {

    static PolicyFactory BLOCK_ALLOWED_HTML_TAGS_POLICY = new HtmlPolicyBuilder().allowStandardUrlProtocols()
            .allowElements("p", "b", "br", "i", "ul", "li", "div", "ol", "pre", "blockquote", "a")
            .allowAttributes("href", "target")
            .onElements("a")
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
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
}
