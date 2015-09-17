package ee.hm.dop.model.mobileid;

public class MobileAuthResponse {

    private String challengeId;

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
