package ee.netgroup.htm.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

@JsonAutoDetect(fieldVisibility = ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticatedUser {
    private User user;
    private String token;

    public void setUser(User user) {
        this.user = user;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUserDto() {
        return user;
    }

    public String getToken() {
        return token;
    }
}
