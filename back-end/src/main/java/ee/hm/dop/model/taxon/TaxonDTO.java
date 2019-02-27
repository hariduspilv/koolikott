package ee.hm.dop.model.taxon;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;

public class TaxonDTO extends Taxon {

    public TaxonDTO() {
    }

    public TaxonDTO(Long id, String name, String translationKey) {
        this.id = id;
        this.name = name;
        this.translationKey = translationKey;
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
    public String getTaxonLevel() {
        return null;
    }
}
