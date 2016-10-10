package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public String getReducedTaxon() {
        List<EducationalContext> taxons = taxonDAO.findAllEducationalContext();
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = null;
        try {
            jsonInString = mapper.writeValueAsString(taxons);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        jsonInString = jsonInString.replaceAll("\"domains\":", "\"children\":");
        jsonInString = jsonInString.replaceAll("\"subjects\":", "\"children\":");
        jsonInString = jsonInString.replaceAll("\"topics\":", "\"children\":");
        jsonInString = jsonInString.replaceAll("\"specializations\":", "\"children\":");
        jsonInString = jsonInString.replaceAll("\"subtopics\":", "\"children\":");
        jsonInString = jsonInString.replaceAll("\"modules\":", "\"children\":");

        jsonInString = jsonInString.replaceAll(",\"children\":\\[\\]", "");

        return jsonInString;
    }

}
