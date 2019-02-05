package ee.netgroup.htm.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;

@JsonAutoDetect(fieldVisibility = ANY)
public class Taxon {
    private Integer id;
    private String name;
    private String nameLowercase;
    private String taxonLevel;
    private Integer parentId;
    private String parentLevel;
    private String level;
    private String translationKey;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameLowercase(String nameLowercase) {
        this.nameLowercase = nameLowercase;
    }

    public void setTaxonLevel(String taxonLevel) {
        this.taxonLevel = taxonLevel;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public void setParentLevel(String parentLevel) {
        this.parentLevel = parentLevel;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNameLowercase() {
        return nameLowercase;
    }

    public String getTaxonLevel() {
        return taxonLevel;
    }

    public Integer getParentId() {
        return parentId;
    }

    public String getParentLevel() {
        return parentLevel;
    }

    public String getLevel() {
        return level;
    }

    public String getTranslationKey() {
        return translationKey;
    }
}
