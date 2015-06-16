package ee.hm.dop.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ee.hm.dop.rest.jackson.map.LanguageDeserializer;
import ee.hm.dop.rest.jackson.map.LanguageSerializer;

/**
 * Created by mart.laus on 10.06.2015.
 */
@Entity
public class LanguageString {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "lang")
    private Language language;

    @Column(nullable = false, columnDefinition = "TEXT", name = "textValue")
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonSerialize(using = LanguageSerializer.class)
    public Language getLanguage() {
        return language;
    }

    @JsonDeserialize(using = LanguageDeserializer.class)
    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
