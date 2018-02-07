package ee.hm.dop.service.reviewmanagement.dto;

import ee.hm.dop.model.taxon.Taxon;

import java.util.List;

public class TaxonWithChildren {

    private Taxon parent;
    private Taxon taxon;
    private List<Long> taxonIds;
    private boolean capped;
    private List<TaxonWithChildren> subjects;

    public TaxonWithChildren() {
    }

    public Taxon getTaxon() {
        return taxon;
    }

    public void setTaxon(Taxon taxon) {
        this.taxon = taxon;
    }

    public List<Long> getTaxonIds() {
        return taxonIds;
    }

    public void setTaxonIds(List<Long> taxonIds) {
        this.taxonIds = taxonIds;
    }

    public boolean isCapped() {
        return capped;
    }

    public void setCapped(boolean capped) {
        this.capped = capped;
    }

    public List<TaxonWithChildren> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<TaxonWithChildren> subjects) {
        this.subjects = subjects;
    }

    public Taxon getParent() {
        return parent;
    }

    public void setParent(Taxon parent) {
        this.parent = parent;
    }
}
