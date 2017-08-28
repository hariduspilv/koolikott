package ee.hm.dop.service.metadata;

import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.model.enums.EducationalContextC;
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
    private TaxonDao taxonDao;

    public Taxon getTaxonById(Long id) {
        return taxonDao.findTaxonById(id);
    }

    public List<EducationalContext> getAllEducationalContext() {
        return taxonDao.findAllEducationalContext();
    }

    public Taxon getTaxonByEstCoreName(String name, Class level) {
        return taxonDao.findTaxonByRepoName(name, EST_CORE_TAXON_MAPPING, level);
    }

    public Taxon findTaxonByTranslation(String name) {
        String translationKey = translationService.getTranslationKeyByTranslation(name);
        if (translationKey == null) {
            return null;
        }

        String taxonName = getTaxonName(translationKey);
        return taxonName == null ? null : taxonDao.findTaxonByName(taxonName);
    }

    private String getTaxonName(String translationKey) {
        List<String> taxonPrefixes = Arrays.asList("MODULE_", "DOMAIN_", "SUBJECT_", "SPECIALIZATION_", "TOPIC_", "SUBTOPIC_");

        for (String prefix : taxonPrefixes) {
            if (translationKey.startsWith(prefix)) {
                return translationKey.replaceFirst("^" + prefix, "");
            }
        }

        for (String context : EducationalContextC.ALL) {
            if (context.equalsIgnoreCase(translationKey)) {
                return context;
            }
        }

        return null;
    }
}
