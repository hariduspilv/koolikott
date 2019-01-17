package ee.hm.dop.model.ekool;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EkoolToken {

    @JsonProperty("access_token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}