package ee.hm.dop.utils.tokenizer;

import org.apache.solr.client.solrj.util.ClientUtils;

public abstract class DOPToken {

    private String content;

    public DOPToken(String content) {
        this.content = content;
    }

    @Override
    public abstract String toString();

    protected String getEscapedContent() {
        return ClientUtils.escapeQueryChars(content);
    }

    public String getContent() {
        return content;
    }
}
