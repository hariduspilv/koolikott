package ee.hm.dop.service;

import ee.hm.dop.dao.TaxonDAO;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class TaxonService {

    private static final String EST_CORE_TAXON_MAPPING = "EstCoreTaxonMapping";

    @Inject
    private TranslationService translationService;

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

    Taxon findTaxonByTranslation(String name) {
        String translationKey = translationService.getTranslationKeyByTranslation(name);
        if (translationKey == null) {
            return null;
        }

        String taxonName = getTaxonName(translationKey);
        return taxonName == null ? null : taxonDAO.findTaxonByName(taxonName);
    }

    private String getTaxonName(String translationKey) {
        List<String> taxonPrefixes = Arrays.asList("MODULE_", "DOMAIN_", "SUBJECT_", "SPECIALIZATION_", "TOPIC_", "SUBTOPIC_");
        List<String> educationalContexts = Arrays.asList("PRESCHOOLEDUCATION", "BASICEDUCATION", "SECONDARYEDUCATION", "VOCATIONALEDUCATION");

        for (String prefix : taxonPrefixes) {
            if (translationKey.startsWith(prefix)) {
                return translationKey.replaceFirst("^" + prefix, "");
            }
        }

        for (String context : educationalContexts) {
            if (context.equalsIgnoreCase(translationKey)) {
                return context;
            }
        }

        return null;
    }
}
