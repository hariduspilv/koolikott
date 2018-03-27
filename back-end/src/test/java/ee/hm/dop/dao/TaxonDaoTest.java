package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.EducationalContextC;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.utils.TaxonUtils;
import ee.hm.dop.utils.UserUtil;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.*;

public class TaxonDaoTest extends DatabaseTestBase {

    public static final long EDUC_CONT_ID = 1L;
    public static final long DOMAIN_ID = 10L;
    public static final long SUBJECT_ID = 20L;
    public static final long SPECIALIZATION_ID = 40L;
    public static final long MODULE_ID = 50L;
    public static final long TOPIC_ID = 30L;
    public static final long SUBTOPIC_ID = 60L;
    @Inject
    private TaxonDao taxonDao;
    @Inject
    private UserDao userDao;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void findTaxonById() {
        Long id = 11L;
        String name = "ForeignLanguage";

        Domain domain = (Domain) taxonDao.findById(id);

        assertNotNull(domain);
        assertEquals(id, domain.getId());
        assertEquals(name, domain.getName());
    }

    @Test
    public void findAllEducationalContext() {
        List<EducationalContext> educationalContexts = taxonDao.findAllEducationalContext();
        assertEquals(9, educationalContexts.stream().distinct().count());
    }

    @Test
    public void findTaxonByRepoName() {
        Taxon taxon = taxonDao.findTaxonByEstCoreName("Mathematics", Domain.class);

        assertNotNull(taxon);
        assertNotNull(taxon.getId());
    }

    @Test
    public void findTaxonByRepoNameIgnoreCase() {
        Taxon taxon = taxonDao.findTaxonByEstCoreName("matheMAtics", Domain.class);

        assertNotNull(taxon);
        assertNotNull(taxon.getId());
    }

    @Test
    public void moderator_can_get_taxons_with_children() throws Exception {
        User user = userDao.findUserByIdCode(USER_MODERATOR.idCode);
        List<Long> taxons = taxonDao.getUserTaxonsWithChildren(user);
        assertTrue(CollectionUtils.isNotEmpty(taxons));
        System.out.println(taxons);
    }

    @Test
    public void user_must_be_moderator_to_get_taxons_with_children() throws Exception {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(UserUtil.MUST_BE_MODERATOR);
        taxonDao.getUserTaxonsWithChildren(userDao.findUserByIdCode(USER_MATI.idCode));
    }

    @Test
    public void educational_context_has_children_and_children_have_parent() {
        assertTaxon(taxonDao.findById(EDUC_CONT_ID));
    }

    @Test
    public void domain_has_children_and_children_have_parent() {
        assertTaxon(taxonDao.findById(DOMAIN_ID));
    }

    @Test
    public void subject_has_children_and_children_have_parent() {
        assertTaxon(taxonDao.findById(SUBJECT_ID));
    }

    @Test
    public void specialization_has_children_and_children_have_parent() {
        assertTaxon(taxonDao.findById(SPECIALIZATION_ID));
    }

    @Test
    public void module_has_children_and_children_have_parent() {
        assertTaxon(taxonDao.findById(MODULE_ID));
    }

    @Test
    public void topic_has_children_and_children_have_parent() {
        assertTaxon(taxonDao.findById(TOPIC_ID));
    }

    @Test
    public void subtopic_has_children_and_children_have_parent() {
        assertTaxon(taxonDao.findById(SUBTOPIC_ID));
    }

    private void assertTaxon(Taxon parent) {
        List<Long> taxonWithChildrenIds = taxonDao.getTaxonWithChildren(parent);
        List<Taxon> taxonWithChildren = taxonDao.findById(taxonWithChildrenIds);
        for (Taxon child : taxonWithChildren) {
            assertNotNull(TaxonUtils.getParent(parent.getClass(), child));
            assertNotNull(TaxonUtils.getParent(parent, child));
        }
    }
}
