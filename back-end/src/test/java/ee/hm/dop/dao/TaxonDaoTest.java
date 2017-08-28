package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.enums.EducationalContextC;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TaxonDaoTest extends DatabaseTestBase {

    @Inject
    private TaxonDao taxonDao;

    @Test
    public void findTaxonById() {
        Long id = 11L;
        String name = "ForeignLanguage";

        Domain domain = (Domain) taxonDao.findTaxonById(id);

        assertNotNull(domain);
        assertEquals(id, domain.getId());
        assertEquals(name, domain.getName());
    }

    @Test
    public void findEducationalContextByName() {
        Long id = 1L;
        String name = EducationalContextC.PRESCHOOLEDUCATION;

        EducationalContext educationalContext = (EducationalContext) taxonDao.findContextByName(name, TaxonDao.EDUCATIONAL_CONTEXT);

        assertEducationalContext(id, name, educationalContext);
    }

    @Test
    public void findEducationalContextByNameWrongCase() {
        Long id = 1L;
        String name = EducationalContextC.PRESCHOOLEDUCATION;

        EducationalContext educationalContext = (EducationalContext) taxonDao.findContextByName("preschooleducation", TaxonDao.EDUCATIONAL_CONTEXT);

        assertEducationalContext(id, name, educationalContext);
    }

    private void assertEducationalContext(Long id, String name, EducationalContext educationalContext) {
        assertNotNull(educationalContext);
        assertNotNull(educationalContext.getId());
        assertEquals(id, educationalContext.getId());
        assertEquals(name, educationalContext.getName());
        assertEquals(2, educationalContext.getDomains().size());
    }

    @Test
    public void findAllEducationalContext() {
        List<EducationalContext> educationalContexts = taxonDao.findAllEducationalContext();
        assertEquals(9, educationalContexts.stream().distinct().count());
    }

    @Test
    public void findTaxonByRepoName() {
        Taxon taxon = taxonDao.findTaxonByRepoName("Mathematics", "EstCoreTaxonMapping", Domain.class);

        assertNotNull(taxon);
        assertNotNull(taxon.getId());
    }

    @Test
    public void findTaxonByRepoNameIgnoreCase() {
        Taxon taxon = taxonDao.findTaxonByRepoName("matheMAtics", "EstCoreTaxonMapping", Domain.class);

        assertNotNull(taxon);
        assertNotNull(taxon.getId());
    }
}
