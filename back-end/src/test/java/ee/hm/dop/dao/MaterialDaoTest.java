package ee.hm.dop.dao;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.RollbackException;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.TargetGroupEnum;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.utils.DbUtils;
import org.joda.time.DateTime;
import org.junit.Test;

public class MaterialDaoTest extends DatabaseTestBase {

    @Inject
    private MaterialDao materialDao;

    @Test
    public void find() {
        long materialId = 1;
        Material material = materialDao.findByIdNotDeleted(materialId);
        assertMaterial1(material);
    }

    @Test
    public void findDeletedMaterial() {
        long materialId = 11;
        Material material = materialDao.findByIdNotDeleted(materialId);
        assertNull(material);
    }

    @Test
    public void authors() {
        Material material = materialDao.findByIdNotDeleted(2);
        assertEquals(2, material.getAuthors().size());
        assertEquals("Isaac", material.getAuthors().get(0).getName());
        assertEquals("John Newton", material.getAuthors().get(0).getSurname());
        assertEquals("Leonardo", material.getAuthors().get(1).getName());
        assertEquals("Fibonacci", material.getAuthors().get(1).getSurname());
    }

    @Test
    public void materialLanguage() {
        Material material1 = materialDao.findByIdNotDeleted(2);
        assertEquals("rus", material1.getLanguage().getCode());

        Material material2 = materialDao.findByIdNotDeleted(1);
        assertEquals("est", material2.getLanguage().getCode());
    }

    @Test
    public void materialResourceType() {
        Material material1 = materialDao.findByIdNotDeleted(1);
        assertEquals("TEXTBOOK1", material1.getResourceTypes().get(0).getName());

        Material material2 = materialDao.findByIdNotDeleted(1);
        assertEquals("EXPERIMENT1", material2.getResourceTypes().get(1).getName());
    }

    @Test
    public void materialTaxon() {
        Material material1 = materialDao.findByIdNotDeleted(1);
        assertEquals("BASICEDUCATION", material1.getTaxons().get(0).getName());
        assertEquals("Biology", material1.getTaxons().get(1).getName());
    }

    @Test
    public void materialLicense() {
        Material material = materialDao.findByIdNotDeleted(1);
        assertEquals("CCBY", material.getLicenseType().getName());
    }

    @Test
    public void materialPublisher() {
        Material material = materialDao.findByIdNotDeleted(1);
        assertEquals("Koolibri", material.getPublishers().get(0).getName());
        assertEquals("http://www.pegasus.ee", material.getPublishers().get(1).getWebsite());
    }

    @Test
    public void materialViews() {
        Material material = materialDao.findByIdNotDeleted(3);
        assertEquals(Long.valueOf(300), material.getViews());
    }

    @Test
    public void findAllById() {
        List<Long> idList = new ArrayList<>();
        idList.add((long) 5);
        idList.add((long) 7);
        idList.add((long) 3);

        List<Long> expectedIdList = new ArrayList<>(idList);
        idList.add((long) 11); // deleted, should not return

        List<LearningObject> result = materialDao.findAllById(idList);

        assertNotNull(result);
        assertEquals(3, result.size());

        for (LearningObject material : result) {
            expectedIdList.remove(material.getId());
        }

        assertTrue(expectedIdList.isEmpty());
    }

    @Test
    public void findAllByIdNoResult() {
        List<Long> idList = new ArrayList<>();
        idList.add((long) 1155);

        List<LearningObject> result = materialDao.findAllById(idList);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void findAllByIdEmptyList() {
        List<LearningObject> result = materialDao.findAllById(new ArrayList<>());

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void materialAddedDate() {
        Material material = materialDao.findByIdNotDeleted(1);
        assertEquals(new DateTime("1999-01-01T02:00:01.000+02:00"), material.getAdded());
    }

    @Test
    public void materialUpdatedDate() {
        Material material = materialDao.findByIdNotDeleted(2);
        assertEquals(new DateTime("1995-07-12T09:00:01.000+00:00"), material.getUpdated());
    }

    @Test
    public void materialTags() {
        Material material = materialDao.findByIdNotDeleted(2);

        assertEquals(4, material.getTags().size());
        assertEquals("matemaatika", material.getTags().get(0).getName());
        assertEquals("mathematics", material.getTags().get(1).getName());
        assertEquals("Математика", material.getTags().get(2).getName());
        assertEquals("учебник", material.getTags().get(3).getName());

    }

    @Test
    public void createMaterial() {
        Material material = new Material();
        material.setSource("asd");
        material.setAdded(new DateTime());
        material.setViews((long) 123);

        Picture picture = new OriginalPicture();
        picture.setId(1);
        material.setPicture(picture);

        Material updated = (Material) materialDao.createOrUpdate(material);

        Material newMaterial = materialDao.findByIdNotDeleted(updated.getId());

        assertEquals(material.getSource(), newMaterial.getSource());
        assertEquals(material.getAdded(), newMaterial.getAdded());
        assertEquals(material.getViews(), newMaterial.getViews());
        assertEquals(material.getPicture().getId(), newMaterial.getPicture().getId());
        assertNull(newMaterial.getUpdated());

        materialDao.remove(newMaterial);
    }

    @Test
    public void findMaterialWith2Subjects() {
        Material material = materialDao.findByIdNotDeleted(6);
        List<Taxon> taxons = material.getTaxons();
        assertNotNull(taxons);
        assertEquals(2, taxons.size());
        Subject biology = (Subject) taxons.get(0);
        assertEquals(new Long(20), biology.getId());
        assertEquals("Biology", biology.getName());
        Subject math = (Subject) taxons.get(1);
        assertEquals(new Long(21), math.getId());
        assertEquals("Mathematics", math.getName());
    }

    @Test
    public void findMaterialWithNoTaxon() {
        Material material = materialDao.findByIdNotDeleted(8);
        List<Taxon> taxons = material.getTaxons();
        assertNotNull(taxons);
        assertEquals(0, taxons.size());
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifier() {
        Repository repository = new Repository();
        repository.setId(1l);

        Material material = materialDao.findByRepositoryAndRepositoryIdentifier(repository, "isssiiaawej");
        assertMaterial1(material);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierWhenRepositoryDoesNotExists() {
        Repository repository = new Repository();
        repository.setId(10l);

        Material material = materialDao.findByRepositoryAndRepositoryIdentifier(repository, "isssiiaawej");
        assertNull(material);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierWhenMaterialIsDeleted() {
        Repository repository = new Repository();
        repository.setId(1L);

        Material material = materialDao.findByRepositoryAndRepositoryIdentifier(repository, "isssiiaawejdsada4564");
        assertNotNull(material);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierWhenRepositoryIdentifierDoesNotExist() {
        Repository repository = new Repository();
        repository.setId(1l);

        Material material = materialDao.findByRepositoryAndRepositoryIdentifier(repository, "SomeRandomIdenetifier");
        assertNull(material);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierNullRepositoryIdAndNullRepositoryIdentifier() {
        Repository repository = new Repository();

        Material material = materialDao.findByRepositoryAndRepositoryIdentifier(repository, null);
        assertNull(material);
    }

    @Test
    public void findByCreator() {
        User creator = new User();
        creator.setId(1L);

        List<LearningObject> materials = materialDao.findByCreator(creator, 0, Integer.MAX_VALUE);

        // Should not return material 11 which is deleted
        assertEquals(3, materials.size());
        assertEquals(Long.valueOf(8), materials.get(0).getId());
        assertEquals(Long.valueOf(4), materials.get(1).getId());
        assertEquals(Long.valueOf(1), materials.get(2).getId());
        assertMaterial1((Material) materials.get(2));
    }

    @Test
    public void update() {
        Material changedMaterial = new Material();
        changedMaterial.setId(9l);
        changedMaterial.setSource("http://www.chaged.it.com");
        DateTime now = new DateTime();
        changedMaterial.setAdded(now);
        Long views = 234l;
        changedMaterial.setViews(views);
        changedMaterial.setUpdated(now);

        materialDao.createOrUpdate(changedMaterial);

        Material material = materialDao.findByIdNotDeleted(9);
        assertEquals("http://www.chaged.it.com", changedMaterial.getSource());
        assertEquals(now, changedMaterial.getAdded());
        DateTime updated = changedMaterial.getUpdated();
        assertTrue(updated.isEqual(now) || updated.isAfter(now));
        assertEquals(views, changedMaterial.getViews());

        // Restore to original values
        material.setSource("http://www.chaging.it.com");
        material.setAdded(new DateTime("1911-09-01T00:00:01"));
        material.setViews(0l);
        material.setUpdated(null);

        materialDao.createOrUpdate(changedMaterial);
    }

    @Test
    public void updateCreatingNewLanguage() {
        Material originalMaterial = materialDao.findByIdNotDeleted(1);

        Language newLanguage = new Language();
        newLanguage.setName("Newlanguage");
        newLanguage.setCode("nlg");

        originalMaterial.setLanguage(newLanguage);

        try {
            materialDao.createOrUpdate(originalMaterial);

            // Have to close the transaction to get the error
            DbUtils.closeTransaction();
            fail("Exception expected.");
        } catch (RollbackException e) {
            String expectedMessage = "org.hibernate.TransientPropertyValueException: "
                    + "object references an unsaved transient instance - "
                    + "save the transient instance before flushing : "
                    + "ee.hm.dop.model.Material.language -> ee.hm.dop.model.Language";
            assertEquals(expectedMessage, e.getCause().getMessage());
        }
    }

    @Test
    public void updateCreatingNewResourceType() {
        Material originalMaterial = materialDao.findByIdNotDeleted(1);

        ResourceType newResourceType = new ResourceType();
        newResourceType.setName("NewType");

        List<ResourceType> newResourceTypes = new ArrayList<>();
        newResourceTypes.add(newResourceType);

        originalMaterial.setResourceTypes(newResourceTypes);

        try {
            materialDao.createOrUpdate(originalMaterial);

            // Have to close the transaction to get the error
            DbUtils.closeTransaction();
            fail("Exception expected.");
        } catch (RollbackException e) {
            String expectedMessage = "org.hibernate.TransientObjectException: "
                    + "object references an unsaved transient instance - "
                    + "save the transient instance before flushing: ee.hm.dop.model.ResourceType";
            assertEquals(expectedMessage, e.getCause().getMessage());
        }
    }

    @Test
    public void updateCreatingNewTaxon() {
        Material originalMaterial = materialDao.findByIdNotDeleted(1);

        Subject newSubject = new Subject();
        newSubject.setName("New Subject");

        List<Taxon> newTaxons = new ArrayList<>();
        newTaxons.add(newSubject);

        originalMaterial.setTaxons(newTaxons);

        try {
            materialDao.createOrUpdate(originalMaterial);

            // Have to close the transaction to get the error
            DbUtils.closeTransaction();
            fail("Exception expected.");
        } catch (RollbackException e) {
            String expectedMessage = "org.hibernate.TransientObjectException: "
                    + "object references an unsaved transient instance - "
                    + "save the transient instance before flushing: ee.hm.dop.model.taxon.Taxon";
            assertEquals(expectedMessage, e.getCause().getMessage());
        }
    }

    @Test
    public void updateCreatingNewLicenseType() {
        Material originalMaterial = materialDao.findByIdNotDeleted(1);

        LicenseType newLicenseType = new LicenseType();
        newLicenseType.setName("NewLicenseTypeTpFail");
        originalMaterial.setLicenseType(newLicenseType);

        try {
            materialDao.createOrUpdate(originalMaterial);

            // Have to close the transaction to get the error
            DbUtils.closeTransaction();
            fail("Exception expected.");
        } catch (RollbackException e) {
            String expectedMessage = "org.hibernate.TransientPropertyValueException: "
                    + "object references an unsaved transient instance - "
                    + "save the transient instance before flushing : "
                    + "ee.hm.dop.model.Material.licenseType -> ee.hm.dop.model.LicenseType";
            assertEquals(expectedMessage, e.getCause().getMessage());
        }
    }

    @Test
    public void updateCreatingNewRepository() {
        Material originalMaterial = materialDao.findByIdNotDeleted(1);

        Repository newRepository = new Repository();
        newRepository.setBaseURL("www.url.com");
        newRepository.setSchema("newSchema");
        originalMaterial.setRepository(newRepository);

        try {
            materialDao.createOrUpdate(originalMaterial);

            // Have to close the transaction to get the error
            DbUtils.closeTransaction();
            fail("Exception expected.");
        } catch (RollbackException e) {
            String expectedMessage = "org.hibernate.TransientPropertyValueException: "
                    + "object references an unsaved transient instance - "
                    + "save the transient instance before flushing : "
                    + "ee.hm.dop.model.Material.repository -> ee.hm.dop.model.Repository";
            assertEquals(expectedMessage, e.getCause().getMessage());
        }
    }

    @Test
    public void delete() {
        Material material = materialDao.findByIdNotDeleted(10);
        materialDao.delete(material);

        Material deletedMaterial = materialDao.findByIdNotDeleted(10);
        assertNull(deletedMaterial);
    }

    @Test
    public void deleteMaterialDoesNotExist() {
        Material material = new Material();

        try {
            materialDao.delete(material);
            fail("Exception expected");
        } catch (InvalidParameterException e) {
            assertEquals("LearningObject does not exist.", e.getMessage());
        }
    }

    @Test
    public void isPaidTrue() {
        Material material = materialDao.findByIdNotDeleted(1);
        assertTrue(material.isPaid());
    }

    @Test
    public void isPaidFalse() {
        Material material = materialDao.findByIdNotDeleted(9);
        assertFalse(material.isPaid());

    }

    @Test
    public void isEmbeddedWhenNoRepository() {
        Material material = materialDao.findByIdNotDeleted(3);
        assertFalse(material.isEmbeddable());
    }

    @Test
    public void isEmbeddedWhenEstonianRepo() {
        Material material = materialDao.findByIdNotDeleted(12);
        assertTrue(material.isEmbeddable());
    }

    @Test
    public void isEmbeddedWhenNotEstonianRepo() {
        Material material = materialDao.findByIdNotDeleted(1);
        assertFalse(material.isEmbeddable());
    }

    @Test
    public void getMaterialsBySource1() {
        List<Material> materials = materialDao.findBySource("en.wikipedia.org/wiki/Power_Architecture", false);
        assertEquals(2, materials.size());
    }

    @Test
    public void getMaterialsBySource2() {
        List<Material> materials = materialDao.findBySource("youtube.com/watch?v=gSWbx3CvVUk", false);
        assertEquals(1, materials.size());
    }

    @Test
    public void getMaterialsBySource3() {
        List<Material> materials = materialDao.findBySource("www.youtube.com/watch?v=gSWbx3CvVUk", false);
        assertEquals(1, materials.size());
    }

    @Test
    public void getMaterialsBySource4() {
        List<Material> materials = materialDao.findBySource("https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes", false);
        assertEquals(1, materials.size());
    }

    private void assertMaterial1(Material material) {
        assertEquals(2, material.getTitles().size());
        assertEquals("Matemaatika õpik üheksandale klassile", material.getTitles().get(0).getText());
        assertEquals(2, material.getDescriptions().size());
        assertEquals("Test description in estonian. (Russian available)", material.getDescriptions().get(0).getText());
        Language descriptionLanguage = material.getDescriptions().get(0).getLanguage();
        assertEquals("est", descriptionLanguage.getCode());
        assertEquals("Estonian", descriptionLanguage.getName());
        Language language = material.getLanguage();
        assertNotNull(language);
        assertEquals("est", language.getCode());
        assertEquals("Estonian", language.getName());
        assertEquals("et", language.getCodes().get(0));
        assertNotNull(material.getPicture());
        assertNotNull(material.getTaxons());
        assertEquals(2, material.getTaxons().size());
        assertEquals(new Long(2), material.getTaxons().get(0).getId());

        Subject biology = (Subject) material.getTaxons().get(1);
        assertEquals(new Long(20), biology.getId());
        assertEquals(2, biology.getDomain().getSubjects().size());
        assertEquals(2, biology.getDomain().getEducationalContext().getDomains().size());

        assertEquals(new Long(1), material.getRepository().getId());
        assertEquals("http://repo1.ee", material.getRepository().getBaseURL());
        assertEquals("isssiiaawej", material.getRepositoryIdentifier());
        assertEquals(new Long(1), material.getCreator().getId());
        assertFalse(material.isEmbeddable());

        assertEquals(2, material.getTargetGroups().size());
        assertTrue(TargetGroupEnum.containsTargetGroup(material.getTargetGroups(), TargetGroupEnum.ZERO_FIVE));
        assertTrue(TargetGroupEnum.containsTargetGroup(material.getTargetGroups(), TargetGroupEnum.SIX_SEVEN));
        assertTrue(material.isSpecialEducation());
        assertEquals("Lifelong_learning_and_career_planning", material.getCrossCurricularThemes().get(0).getName());
        assertEquals("Cultural_and_value_competence", material.getKeyCompetences().get(0).getName());

        Recommendation recommendation = material.getRecommendation();
        assertNotNull(recommendation);
        assertEquals(Long.valueOf(1L), recommendation.getId());
    }
}
