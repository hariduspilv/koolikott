package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.TaxonDAO;
import ee.hm.dop.model.EducationalContext;
import ee.hm.dop.model.Taxon;

public class TaxonService {

    @Inject
    private TaxonDAO taxonDAO;

    public Taxon getTaxonById(Long id) {
        return taxonDAO.findTaxonById(id);
    }

    public EducationalContext getEducationalContextByName(String name) {
        return taxonDAO.findEducationalContextByName(name);
    }

    public List<EducationalContext> getAllEducationalContext() {
        return taxonDAO.findAllEducationalContext();
    }

}
