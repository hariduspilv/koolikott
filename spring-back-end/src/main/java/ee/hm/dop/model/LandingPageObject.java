package ee.hm.dop.model;

import java.util.List;

public class LandingPageObject {

    private List<LandingPageString> notices;
    private List<LandingPageString> descriptions;

    public LandingPageObject() {
    }

    public LandingPageObject(List<LandingPageString> notices, List<LandingPageString> descriptions) {
        this.notices = notices;
        this.descriptions = descriptions;
    }

    public List<LandingPageString> getNotices() {
        return notices;
    }

    public void setNotices(List<LandingPageString> notices) {
        this.notices = notices;
    }

    public List<LandingPageString> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<LandingPageString> descriptions) {
        this.descriptions = descriptions;
    }

 
}
