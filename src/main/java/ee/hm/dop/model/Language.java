package ee.hm.dop.model;

import static javax.persistence.FetchType.EAGER;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

/**
 * This is a mapping for ISO 639. For more information @see <a
 * href="http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes" >wikipedia</a>
 *
 * @author Jordan Silva
 */
@Entity(name = "LanguageTable")
public class Language {

    @ElementCollection(fetch = EAGER)
    @CollectionTable(name = "LanguageKeyCodes", joinColumns = @JoinColumn(name = "lang"))
    @Column(name = "code")
    List<String> codes;
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String code;

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
}
