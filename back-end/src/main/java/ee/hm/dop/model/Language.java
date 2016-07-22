package ee.hm.dop.model;

import static javax.persistence.FetchType.EAGER;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ee.hm.dop.rest.jackson.map.LanguageDeserializer;
import ee.hm.dop.rest.jackson.map.LanguageSerializer;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This is a mapping for ISO 639. For more information @see <a
 * href="http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes" >wikipedia</a>
 *
 * @author Jordan Silva
 */
@Entity(name = "LanguageTable")
@JsonSerialize(using = LanguageSerializer.class)
@JsonDeserialize(using = LanguageDeserializer.class)
public class Language {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    @ElementCollection(fetch = EAGER)
    @CollectionTable(name = "LanguageKeyCodes", joinColumns = @JoinColumn(name = "lang"))
    @Column(name = "code")
    List<String> codes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).append(name).build();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(3, 37) //
                .append(name) //
                .append(code) //
                .append(codes) //
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Language)) {
            return false;
        }

        Language other = (Language) obj;

        return new EqualsBuilder().append(name, other.name) //
                .append(code, other.code) //
                .append(codes, other.codes) //
                .isEquals();
    }
}
