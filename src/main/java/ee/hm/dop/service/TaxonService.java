package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.TaxonDAO;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;

public class TaxonService {

    public static final String EST_CORE_TAXON_MAPPING = "EstCoreTaxonMapping";
    public static final String WARAMU_TAXON_MAPPING = "WaramuTaxonMapping";

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

    public Taxon getTaxonByWaramuName(String name, Class level) {
        return taxonDAO.findTaxonByRepoName(name, WARAMU_TAXON_MAPPING, level);
    }

    public Taxon getTaxonByEstCoreName(String name, Class level) {
        return taxonDAO.findTaxonByRepoName(name, EST_CORE_TAXON_MAPPING, level);
    }

}
