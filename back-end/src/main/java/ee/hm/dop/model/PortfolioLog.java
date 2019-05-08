package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.model.interfaces.IPortfolio;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortfolioLog extends LearningObjectLog {

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
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime publishedAt;

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
        this.summary = summary;
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

    public DateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(DateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}
