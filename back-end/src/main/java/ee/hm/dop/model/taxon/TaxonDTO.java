package ee.hm.dop.model.taxon;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
