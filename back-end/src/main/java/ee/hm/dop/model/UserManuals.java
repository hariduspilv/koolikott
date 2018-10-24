package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
public class UserManuals implements AbstractEntity{

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "createdBy")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User createdBy;

    @Column(nullable = false)
    private String title;

    @Column
    private String url;

    @Column
    private String urlToText;

    @Column(nullable = false)
    private boolean deleted;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToText() { return urlToText; }

    public void setUrlToText(String urlToText) { this.urlToText = urlToText; }

    public DateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(DateTime createdAt) { this.createdAt = createdAt; }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
