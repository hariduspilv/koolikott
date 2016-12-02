package ee.hm.dop.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.TaxonDAO;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;

public class TaxonService {

    private static final String EST_CORE_TAXON_MAPPING = "EstCoreTaxonMapping";

    @Inject
    private TaxonDAO taxonDAO;

    public Taxon getTaxonById(Long id) {
        return taxonDAO.findTaxonById(id);
    }

    public List<EducationalContext> getAllEducationalContext() {
        return taxonDAO.findAllEducationalContext();
    }

    public Taxon getTaxonByEstCoreName(String name, Class level) {
        return taxonDAO.findTaxonByRepoName(name, EST_CORE_TAXON_MAPPING, level);
    }
}
