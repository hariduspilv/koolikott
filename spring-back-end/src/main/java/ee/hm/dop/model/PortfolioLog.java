package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.interfaces.IPortfolio;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static ee.hm.dop.model.LearningObject.LO_ALLOWED_HTML_TAGS_POLICY;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortfolioLog extends LearningObjectLog  {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @OneToMany(fetch = EAGER, cascade = ALL)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "portfolio")
    @OrderColumn(name = "chapterOrder")
    private List<ChapterLog> chapters;

    @ManyToOne
    @JoinColumn(name = "originalCreator", nullable = false)
    private User originalCreator;

    @Column
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private LocalDateTime publishedAt;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = getSanitizedHTML(summary);
    }

    public List<ChapterLog> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterLog> chapters) {
        this.chapters = chapters;
    }

    public User getOriginalCreator() {
        return originalCreator;
    }

    public void setOriginalCreator(User originalCreator) {
        this.originalCreator = originalCreator;
    }

    private String getSanitizedHTML(String summary) {
        if (summary != null) {
            summary = LO_ALLOWED_HTML_TAGS_POLICY.sanitize(summary);
        }
        return summary;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}

