package ee.hm.dop.model.taxon;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;

/**
 * Created by mart on 9.12.16.
 */
public class TaxonDTO extends Taxon {

    public TaxonDTO() {
    }

    public TaxonDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonIgnore
    @Override
    public Taxon getParent() {
        return this;
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


}
