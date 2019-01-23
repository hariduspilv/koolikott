package ee.hm.dop.model.taxon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Set;

public class TaxonDTO extends Taxon {

    private String translationKey;

    public TaxonDTO() {
    }

    public TaxonDTO(Long id, String name, String level) {
        this.id = id;
        this.name = name;
        this.translationKey = level.toUpperCase() + "_" + name.toUpperCase();
    }

    @JsonIgnore
    @Override
    public String getSolrLevel() {
        return null;
    }

    @JsonIgnore
    @Override
    public Taxon getParent() {
        return null;
    }

    @JsonIgnore
    @Override
    public Set<? extends Taxon> getChildren() {
        return null;
    }

    @Override
    public Long getParentId() {
        return null;
    }

    @Override
    public String getParentLevel() {
        return null;
    }

    @Override
    public String getLevel() {
        return null;
    }

    @Override
    public String getTaxonLevel(){
        return null;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey(){
        return this.translationKey;
    }
}
