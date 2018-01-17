package ee.hm.dop.service.reviewmanagement.dto;

import ee.hm.dop.model.taxon.Taxon;

import java.util.List;

public class TaxonWithChildren {

    private Taxon taxon;
    private List<Long> taxonWithChildren;

    public TaxonWithChildren(Taxon taxon, List<Long> taxonWithChildren) {
        this.taxon = taxon;
        this.taxonWithChildren = taxonWithChildren;
    }

    public Taxon getTaxon() {
        return taxon;
    }

    public void setTaxon(Taxon taxon) {
        this.taxon = taxon;
    }

    public List<Long> getTaxonWithChildren() {
        return taxonWithChildren;
    }

    public void setTaxonWithChildren(List<Long> taxonWithChildren) {
        this.taxonWithChildren = taxonWithChildren;
    }
}
