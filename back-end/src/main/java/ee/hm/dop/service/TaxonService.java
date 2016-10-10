package ee.hm.dop.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.hm.dop.dao.TaxonDAO;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.TaxonDTO;

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

    public List<TaxonDTO> getReducedTaxon() {
        List<Taxon> taxons = taxonDAO.findReducedTaxon();
        List<TaxonDTO> result = new ArrayList<>();

        for (Taxon taxon : taxons){
            TaxonDTO taxonDTO = new TaxonDTO(taxon);
            result.add(taxonDTO);
        }

        return result;
    }

}
