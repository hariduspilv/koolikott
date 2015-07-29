package ee.hm.dop.utils.tokens;

public abstract class DOPToken {

    private String content;

    public DOPToken(String content) {
        this.setContent(content);
    }

    @Override
    public abstract String toString();

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
