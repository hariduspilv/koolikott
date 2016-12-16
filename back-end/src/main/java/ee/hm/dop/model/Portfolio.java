package ee.hm.dop.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.rest.jackson.map.TaxonDeserializer;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class Portfolio extends LearningObject implements Searchable {

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "taxon")
    @JsonDeserialize(using = TaxonDeserializer.class)
    private Taxon taxon;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @OneToMany(fetch = EAGER, cascade = ALL)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "portfolio")
    @OrderColumn(name = "chapterOrder")
    private List<Chapter> chapters;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @ManyToOne
    @JoinColumn(name = "originalCreator", nullable = false)
    private User originalCreator;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Taxon getTaxon() {
        return taxon;
    }

    public void setTaxon(Taxon taxon) {
        this.taxon = taxon;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {

        this.summary = getSanitizedHTML(summary);
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public User getOriginalCreator() {
        return originalCreator;
    }

    public void setOriginalCreator(User originalCreator) {
        this.originalCreator = originalCreator;
    }

    private String getSanitizedHTML(String summary) {
        if (summary != null) {
            summary = ALLOWED_HTML_TAGS_POLICY.sanitize(summary);
        }

        return summary;
    }
}
