package ee.hm.dop.dao;

import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.TaxonPosition;

public class TaxonPositionDao extends AbstractDao<TaxonPosition> {


    public TaxonPosition findByTaxon(Taxon taxon){
        return findByField("taxon", taxon);
    }

}
