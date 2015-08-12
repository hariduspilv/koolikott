package ee.hm.dop.model;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).append(language) //
                .append(text) //
                .build();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(5, 41).append(language) //
                .append(text) //
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof LanguageString)) {
            return false;
        }

        LanguageString other = (LanguageString) obj;

        return new EqualsBuilder().append(language, other.language) //
                .append(text, other.text) //
                .isEquals();
    }
}
