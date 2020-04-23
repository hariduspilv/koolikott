package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class UserManuals implements AbstractEntity{

    public UserManuals(LocalDateTime createdAt, User createdBy, String title, String url, String textUrl) {
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.title = title;
        this.url = url;
        this.textUrl = textUrl;
    }

    public UserManuals() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "createdBy")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User createdBy;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(columnDefinition = "TEXT")
    private String textUrl;

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

    public String getTextUrl() { return textUrl; }

    public void setTextUrl(String textUrl) { this.textUrl = textUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

}
