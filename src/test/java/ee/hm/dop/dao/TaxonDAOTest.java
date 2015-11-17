package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Domain;
import ee.hm.dop.model.EducationalContext;

public class TaxonDAOTest extends DatabaseTestBase {

    @Inject
    private TaxonDAO taxonDAO;

    @Test
    public void findEducationalContextByName() {
        Long id = new Long(1);
        String name = "PRESCHOOLEDUCATION";

        EducationalContext educationalContext = taxonDAO.findEducationalContextByName(name);

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
    public void findAllDomainsByEducationalContext() {
        EducationalContext educationalContext = taxonDAO.findEducationalContextByName("PRESCHOOLEDUCATION");
        List<Domain> domains = taxonDAO.findAllDomainsByEducationalContext(educationalContext);
        assertEquals(2, domains.stream().distinct().count());

        List<String> domainNames = domains.stream().map(d -> d.getName()).collect(Collectors.toList());
        assertTrue(domainNames.contains("Mathematics"));
        assertTrue(domainNames.contains("ForeignLanguage"));
    }
}
