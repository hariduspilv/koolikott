package ee.netgroup.htm.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.joda.time.DateTime;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

@JsonAutoDetect(fieldVisibility = ANY)
public class Agreement {
    private Long id;
    private DateTime validFrom;
    private DateTime createdAt;
    private User createdBy;
    private String version;
    private String url;
    private boolean deleted;

    public void setId(Long id) {
        this.id = id;
    }

    public void setValidFrom(DateTime validFrom) {
        this.validFrom = validFrom;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public DateTime getValidFrom() {
        return validFrom;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public String getVersion() {
        return version;
    }

    public String getUrl() {
        return url;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
