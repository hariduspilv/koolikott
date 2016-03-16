package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import org.junit.Test;

public class TaxonDAOTest extends DatabaseTestBase {

    @Inject
    private TaxonDAO taxonDAO;

    @Test
    public void findTaxonById() {
        Long id = new Long(11);
        String name = "ForeignLanguage";

        Domain domain = (Domain) taxonDAO.findTaxonById(id);

        assertNotNull(domain);
        assertEquals(id, domain.getId());
        assertEquals(name, domain.getName());
    }

    @Test
    public void findEducationalContextByName() {
        Long id = new Long(1);
        String name = "PRESCHOOLEDUCATION";

        EducationalContext educationalContext = taxonDAO.findEducationalContextByName(name);

        assertEducationalContext(id, name, educationalContext);
    }

    @Test
    public void findEducationalContextByNameWrongCase() {
        Long id = new Long(1);
        String name = "PRESCHOOLEDUCATION";

        EducationalContext educationalContext = taxonDAO.findEducationalContextByName("preschooleducation");

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
        List<EducationalContext> educationalContexts = taxonDAO.findAllEducationalContext();
        assertEquals(9, educationalContexts.stream().distinct().count());
    }

    @Test
    public void findTaxonByRepoName() {
        Taxon taxon = taxonDAO.findTaxonByRepoName("Mathematics", "EstCoreTaxonMapping", Domain.class);

        assertNotNull(taxon);
        assertNotNull(taxon.getId());
    }

    @Test
    public void findTaxonByRepoNameIgnoreCase() {
        Taxon taxon = taxonDAO.findTaxonByRepoName("matheMAtics", "EstCoreTaxonMapping", Domain.class);

        assertNotNull(taxon);
        assertNotNull(taxon.getId());
    }
}
