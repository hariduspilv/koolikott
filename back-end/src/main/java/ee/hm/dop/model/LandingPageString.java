package ee.hm.dop.model;

public class LandingPageString {

    private String language;
    private String text;

    public LandingPageString() {
    }

    public LandingPageString(String language, String text) {
        this.language = language;
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
