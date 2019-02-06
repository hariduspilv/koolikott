package ee.netgroup.htm.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.joda.time.DateTime;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

@JsonAutoDetect(fieldVisibility = ANY)
public class UserAgreement {
    private Long id;
    private User user;
    private Agreement agreement;
    private boolean agreed;
    private DateTime createdAt;

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAgreement(Agreement agreement) {
        this.agreement = agreement;
    }

    public void setAgreed(boolean agreed) {
        this.agreed = agreed;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Agreement getAgreement() {
        return agreement;
    }

    public boolean isAgreed() {
        return agreed;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }
}
