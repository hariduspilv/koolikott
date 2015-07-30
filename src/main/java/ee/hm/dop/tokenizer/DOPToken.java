package ee.hm.dop.tokenizer;

import org.apache.solr.client.solrj.util.ClientUtils;

public abstract class DOPToken {

    private String content;

    public DOPToken(String content) {
        this.setContent(content);
    }

    @Override
    public abstract String toString();

    protected String getContentWithEscapedChars() {
        return ClientUtils.escapeQueryChars(content);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
