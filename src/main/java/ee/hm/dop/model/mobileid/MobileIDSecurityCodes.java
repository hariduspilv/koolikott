package ee.hm.dop.model.mobileid;

public class MobileIDSecurityCodes {

    /**
     * Code that is shown on the user's mobile and on the site.
     */
    private String challengeId;

    /**
     * Token that references an AuthenticationState.
     */
    private String token;

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
