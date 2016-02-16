package ee.hm.dop.dao;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertArrayEquals;
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

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.LicenseType;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.utils.DbUtils;

public class MaterialDAOTest extends DatabaseTestBase {

    @Inject
    private MaterialDAO materialDAO;

    @Test
    public void find() {
        long materialId = 1;
        Material material = materialDAO.findByIdNotDeleted(materialId);
        assertMaterial1(material);
    }

    @Test
    public void findDeletedMaterial() {
        long materialId = 11;
        Material material = materialDAO.findByIdNotDeleted(materialId);
        assertNull(material);
    }

    @Test
    public void findNewestMaterials() {
        List<Material> materials = materialDAO.findNewestMaterials(8);
        assertEquals(8, materials.size());
        Material last = null;
        for (Material material : materials) {
            if (last != null) {

                // Check that the materials are in the newest to oldest order
                assertTrue(last.getAdded().compareTo(material.getAdded()) == 1);
            }

            last = material;
            assertNotNull(material.getAdded());

            // Cannot be material 11 because it is deleted
            Assert.assertNotEquals(new Long(11), material.getId());
        }
    }

    @Test
    public void authors() {
        Material material = materialDAO.findByIdNotDeleted(2);
        assertEquals(2, material.getAuthors().size());
        assertEquals("Isaac", material.getAuthors().get(0).getName());
        assertEquals("John Newton", material.getAuthors().get(0).getSurname());
        assertEquals("Leonardo", material.getAuthors().get(1).getName());
        assertEquals("Fibonacci", material.getAuthors().get(1).getSurname());
    }

    @Test
    public void materialLanguage() {
        Material material1 = materialDAO.findByIdNotDeleted(2);
        assertEquals("rus", material1.getLanguage().getCode());

        Material material2 = materialDAO.findByIdNotDeleted(1);
        assertEquals("est", material2.getLanguage().getCode());
    }

    @Test
    public void materialResourceType() {
        Material material1 = materialDAO.findByIdNotDeleted(1);
        assertEquals("TEXTBOOK1", material1.getResourceTypes().get(0).getName());

        Material material2 = materialDAO.findByIdNotDeleted(1);
        assertEquals("EXPERIMENT1", material2.getResourceTypes().get(1).getName());
    }

    @Test
    public void materialTaxon() {
        Material material1 = materialDAO.findByIdNotDeleted(1);
        assertEquals("BASICEDUCATION", material1.getTaxons().get(0).getName());
        assertEquals("Biology", material1.getTaxons().get(1).getName());
    }

    @Test
    public void materialLicense() {
        Material material = materialDAO.findByIdNotDeleted(1);
        assertEquals("CCBY", material.getLicenseType().getName());
    }

    @Test
    public void materialPublisher() {
        Material material = materialDAO.findByIdNotDeleted(1);
        assertEquals("Koolibri", material.getPublishers().get(0).getName());
        assertEquals("http://www.pegasus.ee", material.getPublishers().get(1).getWebsite());
    }

    @Test
    public void materialViews() {
        Material material = materialDAO.findByIdNotDeleted(3);
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

        List<LearningObject> result = materialDAO.findAllById(idList);

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

        List<LearningObject> result = materialDAO.findAllById(idList);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void findAllByIdEmptyList() {
        List<LearningObject> result = materialDAO.findAllById(new ArrayList<>());

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void materialAddedDate() {
        Material material = materialDAO.findByIdNotDeleted(1);
        assertEquals(new DateTime("1999-01-01T02:00:01.000+02:00"), material.getAdded());
    }

    @Test
    public void materialUpdatedDate() {
        Material material = materialDAO.findByIdNotDeleted(2);
        assertEquals(new DateTime("1995-07-12T09:00:01.000+00:00"), material.getUpdated());
    }

    @Test
    public void materialTags() {
        Material material = materialDAO.findByIdNotDeleted(2);

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
        String data = "picture";
        byte[] picture = data.getBytes();
        material.setPicture(picture);

        Material updated = (Material) materialDAO.update(material);

        Material newMaterial = materialDAO.findByIdNotDeleted(updated.getId());

        assertEquals(material.getSource(), newMaterial.getSource());
        assertEquals(material.getAdded(), newMaterial.getAdded());
        assertEquals(material.getViews(), newMaterial.getViews());
        assertArrayEquals(material.getPicture(), newMaterial.getPicture());
        assertEquals(material.getHasPicture(), newMaterial.getHasPicture());
        assertNull(newMaterial.getUpdated());

        materialDAO.remove(newMaterial);
    }

    @Test
    public void findPictureByMaterial() {
        Material material = new Material();
        material.setId((long) 1);
        byte[] picture = materialDAO.findNotDeletedPictureByMaterial(material);
        assertNotNull(picture);
    }

    @Test
    public void findPictureByMaterialWhenMaterialIsDeleted() {
        Material material = new Material();
        material.setId(11L);
        byte[] picture = materialDAO.findNotDeletedPictureByMaterial(material);
        assertNull(picture);
    }

    @Test
    public void findPictureByMaterialNoPicture() {
        Material material = new Material();
        material.setId((long) 2);
        byte[] picture = materialDAO.findNotDeletedPictureByMaterial(material);
        assertNull(picture);
    }

    @Test
    public void getHasPictureTrue() {
        Material material = materialDAO.findByIdNotDeleted(1);
        assertTrue(material.getHasPicture());
    }

    @Test
    public void getHasPictureNoPicture() {
        Material material = materialDAO.findByIdNotDeleted(2);
        assertFalse(material.getHasPicture());
    }

    @Test
    public void getHasPicture() {
        Material material = materialDAO.findByIdNotDeleted(1);
        assertTrue(material.getHasPicture());
        byte[] picture = material.getPicture();

        material.setPicture(null);
        material.setHasPicture(false);
        materialDAO.update(material);
        Material material2 = materialDAO.findByIdNotDeleted(1);
        assertFalse(material2.getHasPicture());

        material2.setPicture(picture);
        material2.setHasPicture(true);
        materialDAO.update(material2);

        Material material3 = materialDAO.findByIdNotDeleted(1);
        assertTrue(material3.getHasPicture());
        assertNotNull(material3.getPicture());
    }

    @Test
    public void findMaterialWith2Subjects() {
        Material material = materialDAO.findByIdNotDeleted(6);
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
        Material material = materialDAO.findByIdNotDeleted(8);
        List<Taxon> taxons = material.getTaxons();
        assertNotNull(taxons);
        assertEquals(0, taxons.size());
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifier() {
        Repository repository = new Repository();
        repository.setId(1l);

        Material material = materialDAO.findByRepositoryAndRepositoryIdentifier(repository, "isssiiaawej");
        assertMaterial1(material);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierWhenRepositoryDoesNotExists() {
        Repository repository = new Repository();
        repository.setId(10l);

        Material material = materialDAO.findByRepositoryAndRepositoryIdentifier(repository, "isssiiaawej");
        assertNull(material);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierWhenMaterialIsDeleted() {
        Repository repository = new Repository();
        repository.setId(1L);

        Material material = materialDAO.findByRepositoryAndRepositoryIdentifier(repository, "isssiiaawejdsada4564");
        assertNull(material);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierWhenRepositoryIdentifierDoesNotExist() {
        Repository repository = new Repository();
        repository.setId(1l);

        Material material = materialDAO.findByRepositoryAndRepositoryIdentifier(repository, "SomeRandomIdenetifier");
        assertNull(material);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierNullRepositoryIdAndNullRepositoryIdentifier() {
        Repository repository = new Repository();

        Material material = materialDAO.findByRepositoryAndRepositoryIdentifier(repository, null);
        assertNull(material);
    }

    @Test
    public void findByCreator() {
        User creator = new User();
        creator.setId(1L);

        List<LearningObject> materials = materialDAO.findByCreator(creator);

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

        materialDAO.update(changedMaterial);

        Material material = materialDAO.findByIdNotDeleted(9);
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

        materialDAO.update(changedMaterial);
    }

    @Test
    public void updateCreatingNewLanguage() {
        Material originalMaterial = materialDAO.findByIdNotDeleted(1);

        Language newLanguage = new Language();
        newLanguage.setName("Newlanguage");
        newLanguage.setCode("nlg");

        originalMaterial.setLanguage(newLanguage);

        try {
            materialDAO.update(originalMaterial);

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

    @Inject
    LanguageDAO languageDAO;

    @Test
    public void updateCreatingNewResourceType() {
        Material originalMaterial = materialDAO.findByIdNotDeleted(1);

        ResourceType newResourceType = new ResourceType();
        newResourceType.setName("NewType");

        List<ResourceType> newResourceTypes = new ArrayList<>();
        newResourceTypes.add(newResourceType);

        originalMaterial.setResourceTypes(newResourceTypes);

        try {
            materialDAO.update(originalMaterial);

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
        Material originalMaterial = materialDAO.findByIdNotDeleted(1);

        Subject newSubject = new Subject();
        newSubject.setName("New Subject");

        List<Taxon> newTaxons = new ArrayList<>();
        newTaxons.add(newSubject);

        originalMaterial.setTaxons(newTaxons);

        try {
            materialDAO.update(originalMaterial);

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
        Material originalMaterial = materialDAO.findByIdNotDeleted(1);

        LicenseType newLicenseType = new LicenseType();
        newLicenseType.setName("NewLicenseTypeTpFail");
        originalMaterial.setLicenseType(newLicenseType);

        try {
            materialDAO.update(originalMaterial);

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
        Material originalMaterial = materialDAO.findByIdNotDeleted(1);

        Repository newRepository = new Repository();
        newRepository.setBaseURL("www.url.com");
        newRepository.setSchema("newSchema");
        originalMaterial.setRepository(newRepository);

        try {
            materialDAO.update(originalMaterial);

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
        Material material = materialDAO.findByIdNotDeleted(10);
        materialDAO.delete(material);

        Material deletedMaterial = materialDAO.findByIdNotDeleted(10);
        assertNull(deletedMaterial);
    }

    @Test
    public void deleteMaterialDoesNotExist() {
        Material material = new Material();

        try {
            materialDAO.delete(material);
            fail("Exception expected");
        } catch (InvalidParameterException e) {
            assertEquals("LearningObject does not exist.", e.getMessage());
        }
    }

    @Test
    public void isPaidTrue() {
        Material material = materialDAO.findByIdNotDeleted(1);
        assertTrue(material.isPaid());
    }

    @Test
    public void isPaidFalse() {
        Material material = materialDAO.findByIdNotDeleted(9);
        assertFalse(material.isPaid());

    }

    @Test
    public void isEmbeddedWhenNoRepository() {
        Material material = materialDAO.findByIdNotDeleted(3);
        assertFalse(material.isEmbeddable());
    }

    @Test
    public void isEmbeddedWhenEstonianRepo() {
        Material material = materialDAO.findByIdNotDeleted(12);
        assertTrue(material.isEmbeddable());
    }

    @Test
    public void isEmbeddedWhenNotEstonianRepo() {
        Material material = materialDAO.findByIdNotDeleted(1);
        assertFalse(material.isEmbeddable());
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
        assertTrue(material.getTargetGroups().contains(TargetGroup.ZERO_FIVE));
        assertTrue(material.getTargetGroups().contains(TargetGroup.SIX_SEVEN));
        assertTrue(material.isSpecialEducation());
        assertEquals("Lifelong_learning_and_career_planning", material.getCrossCurricularThemes().get(0).getName());
        assertEquals("Cultural_and_value_competence", material.getKeyCompetences().get(0).getName());

        Recommendation recommendation = material.getRecommendation();
        assertNotNull(recommendation);
        assertEquals(Long.valueOf(1L), recommendation.getId());
    }
}
