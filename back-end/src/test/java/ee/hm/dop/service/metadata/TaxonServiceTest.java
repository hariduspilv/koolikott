package ee.hm.dop.service.metadata;

import ee.hm.dop.model.EstCoreTaxonMapping;
import org.junit.Test;

import static org.junit.Assert.*;

public class TaxonServiceTest {

    @Test
    public void est_core_taxon_mapping_constant_equals_entity_name() throws Exception {
        assertEquals(TaxonService.EST_CORE_TAXON_MAPPING, EstCoreTaxonMapping.class.getSimpleName());
    }
}