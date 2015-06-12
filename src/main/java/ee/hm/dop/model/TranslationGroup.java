package ee.hm.dop.model;

import static javax.persistence.FetchType.EAGER;

import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.UniqueConstraint;

@Entity
@NamedQueries({ @NamedQuery(
        name = "TranslationGroup.findByLanguage",
        query = "SELECT tg FROM TranslationGroup tg WHERE tg.language = :language") })
public class TranslationGroup {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "lang", nullable = false)
    private Language language;

    @ElementCollection(fetch = EAGER)
    @CollectionTable(name = "Translation", uniqueConstraints = @UniqueConstraint(columnNames = { "translationGroup",
            "translationKey" }), joinColumns = @JoinColumn(name = "translationGroup"))
    @MapKeyColumn(name = "translationKey", nullable = false)
    @Column(name = "translation", nullable = false, columnDefinition = "TEXT")
    private Map<String, String> translations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }
}
