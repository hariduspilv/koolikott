package ee.hm.dop.dao;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.RollbackException;

import org.joda.time.DateTime;
import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.EducationalContext;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.LicenseType;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.Subject;
import ee.hm.dop.utils.DbUtils;

public class MaterialDAOTest extends DatabaseTestBase {

    @Inject
    private MaterialDAO materialDAO;

    @Test
    public void find() {
        long materialId = 1;
        Material material = materialDAO.findById(materialId);
        assertMaterial1(material);
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
        }
    }

    @Test
    public void AuthorAndDesc() {
        Material material = materialDAO.findById(1);
        assertMaterial1(material);
    }

    @Test
    public void Authors() {
        Material material = materialDAO.findById(2);
        assertEquals(2, material.getAuthors().size());
        assertEquals("Isaac", material.getAuthors().get(0).getName());
        assertEquals("John Newton", material.getAuthors().get(0).getSurname());
        assertEquals("Leonardo", material.getAuthors().get(1).getName());
        assertEquals("Fibonacci", material.getAuthors().get(1).getSurname());
    }

    @Test
    public void MaterialLanguage() {
        Material material1 = materialDAO.findById(2);
        assertEquals("rus", material1.getLanguage().getCode());

        Material material2 = materialDAO.findById(1);
        assertEquals("est", material2.getLanguage().getCode());
    }

    @Test
    public void MaterialResourceType() {
        Material material1 = materialDAO.findById(1);
        assertEquals("TEXTBOOK1", material1.getResourceTypes().get(0).getName());

        Material material2 = materialDAO.findById(1);
        assertEquals("EXPERIMENT1", material2.getResourceTypes().get(1).getName());
    }

    @Test
    public void MaterialEducationalContext() {
        Material material1 = materialDAO.findById(1);
        assertEquals("PRESCHOOLEDUCATION", material1.getEducationalContexts().get(0).getName());
        assertEquals("GENERALEDUCATION", material1.getEducationalContexts().get(1).getName());
    }

    @Test
    public void MaterialLicense() {
        Material material = materialDAO.findById(1);
        assertEquals("CCBY", material.getLicenseType().getName());
    }

    @Test
    public void MaterialPublisher() {
        Material material = materialDAO.findById(1);
        assertEquals("Koolibri", material.getPublishers().get(0).getName());
        assertEquals("http://www.pegasus.ee", material.getPublishers().get(1).getWebsite());
    }

    @Test
    public void MaterialViews() {
        Material material = materialDAO.findById(3);
        assertEquals(Long.valueOf(300), material.getViews());
    }

    @Test
    public void findAllById() {
        List<Long> idList = new ArrayList<>();
        idList.add((long) 5);
        idList.add((long) 7);
        idList.add((long) 3);

        List<Long> idListCopy = new ArrayList<>(idList);

        List<Material> result = materialDAO.findAllById(idList);

        assertNotNull(result);
        assertEquals(3, result.size());

        for (Material material : result) {
            idListCopy.remove(material.getId());
        }

        assertTrue(idListCopy.isEmpty());
    }

    @Test
    public void findAllByIdNoResult() {
        List<Long> idList = new ArrayList<>();
        idList.add((long) 1155);

        List<Material> result = materialDAO.findAllById(idList);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void findAllByIdEmptyList() {
        List<Material> result = materialDAO.findAllById(new ArrayList<>());

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void MaterialAddedDate() {
        Material material = materialDAO.findById(1);
        assertEquals(new DateTime("1999-01-01T02:00:01.000+02:00"), material.getAdded());
    }

    @Test
    public void MaterialUpdatedDate() {
        Material material = materialDAO.findById(2);
        assertEquals(new DateTime("1995-07-12T09:00:01.000+00:00"), material.getUpdated());
    }

    @Test
    public void MaterialTags() {
        Material material = materialDAO.findById(2);

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

        Material updated = materialDAO.update(material);

        Material newMaterial = materialDAO.findById(updated.getId());

        assertEquals(material.getSource(), newMaterial.getSource());
        assertEquals(material.getAdded(), newMaterial.getAdded());
        assertEquals(material.getViews(), newMaterial.getViews());
        assertArrayEquals(material.getPicture(), newMaterial.getPicture());
        assertEquals(material.getHasPicture(), newMaterial.getHasPicture());

        materialDAO.delete(newMaterial);
    }

    @Test
    public void findPictureByMaterial() {
        Material material = new Material();
        material.setId((long) 1);
        byte[] picture = materialDAO.findPictureByMaterial(material);
        assertNotNull(picture);
    }

    @Test
    public void findPictureByMaterialNoPicture() {
        Material material = new Material();
        material.setId((long) 2);
        byte[] picture = materialDAO.findPictureByMaterial(material);
        assertNull(picture);
    }

    @Test
    public void getHasPictureTrue() {
        Material material = materialDAO.findById(1);
        assertTrue(material.getHasPicture());
    }

    @Test
    public void getHasPictureNoPicture() {
        Material material = materialDAO.findById(2);
        assertFalse(material.getHasPicture());
    }

    @Test
    public void getHasPicture() {
        Material material = materialDAO.findById(1);
        assertTrue(material.getHasPicture());
        byte[] picture = material.getPicture();

        material.setPicture(null);
        material.setHasPicture(false);
        materialDAO.update(material);
        Material material2 = materialDAO.findById(1);
        assertFalse(material2.getHasPicture());

        material2.setPicture(picture);
        material2.setHasPicture(true);
        materialDAO.update(material2);

        Material material3 = materialDAO.findById(1);
        assertTrue(material3.getHasPicture());
        assertNotNull(material3.getPicture());
    }

    @Test
    public void findMaterialWith2Subjects() {
        Material material = materialDAO.findById(6);
        List<Subject> subjects = material.getSubjects();
        assertNotNull(subjects);
        assertEquals(2, subjects.size());
        Subject biology = subjects.get(0);
        assertEquals(new Long(1), biology.getId());
        assertEquals("Biology", biology.getName());
        Subject math = subjects.get(1);
        assertEquals(new Long(2), math.getId());
        assertEquals("Mathematics", math.getName());
    }

    @Test
    public void findMaterialWithNoSubjects() {
        Material material = materialDAO.findById(7);
        List<Subject> subjects = material.getSubjects();
        assertNotNull(subjects);
        assertEquals(0, subjects.size());
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

        Material material = materialDAO.findById(9);
        assertEquals("http://www.chaged.it.com", changedMaterial.getSource());
        assertEquals(now, changedMaterial.getAdded());
        assertEquals(now, changedMaterial.getUpdated());
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
        Material originalMaterial = materialDAO.findById(1);

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
        Material originalMaterial = materialDAO.findById(1);

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
    public void updateCreatingNewSubject() {
        Material originalMaterial = materialDAO.findById(1);

        Subject newSubject = new Subject();
        newSubject.setName("New Subject");

        List<Subject> newResourceTypes = new ArrayList<>();
        newResourceTypes.add(newSubject);

        originalMaterial.setSubjects(newResourceTypes);

        try {
            materialDAO.update(originalMaterial);

            // Have to close the transaction to get the error
            DbUtils.closeTransaction();
            fail("Exception expected.");
        } catch (RollbackException e) {
            String expectedMessage = "org.hibernate.TransientObjectException: "
                    + "object references an unsaved transient instance - "
                    + "save the transient instance before flushing: ee.hm.dop.model.Subject";
            assertEquals(expectedMessage, e.getCause().getMessage());
        }
    }

    @Test
    public void updateCreatingNewEducationalContext() {
        Material originalMaterial = materialDAO.findById(1);

        EducationalContext newEducationalContext = new EducationalContext();
        newEducationalContext.setName("NewEducationalContext");

        List<EducationalContext> newEducationalContexts = new ArrayList<>();
        newEducationalContexts.add(newEducationalContext);

        originalMaterial.setEducationalContexts(newEducationalContexts);

        try {
            materialDAO.update(originalMaterial);

            // Have to close the transaction to get the error
            DbUtils.closeTransaction();
            fail("Exception expected.");
        } catch (RollbackException e) {
            String expectedMessage = "org.hibernate.TransientObjectException: "
                    + "object references an unsaved transient instance - "
                    + "save the transient instance before flushing: ee.hm.dop.model.EducationalContext";
            assertEquals(expectedMessage, e.getCause().getMessage());
        }
    }

    @Test
    public void updateCreatingNewLicenseType() {
        Material originalMaterial = materialDAO.findById(1);

        LicenseType newLicenseType = new LicenseType();
        newLicenseType.setName("NewEducationalContext");
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
        Material originalMaterial = materialDAO.findById(1);

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

    private void assertMaterial1(Material material) {
        assertEquals(2, material.getTitles().size());
        assertEquals("Matemaatika õpik üheksandale klassile", material.getTitles().get(0).getText());
        assertEquals(2, material.getDescriptions().size());
        assertEquals("Test description in estonian. (Russian available)", material.getDescriptions().get(0).getText());
        Language language = material.getDescriptions().get(0).getLanguage();
        assertNotNull(language);
        assertEquals("est", language.getCode());
        assertEquals("Estonian", language.getName());
        assertEquals("et", language.getCodes().get(0));
        assertNotNull(material.getPicture());
        assertNotNull(material.getSubjects());
        assertEquals(1, material.getSubjects().size());
        assertEquals(new Long(1), material.getSubjects().get(0).getId());
        assertEquals(new Long(1), material.getRepository().getId());
        assertEquals("http://repo1.ee", material.getRepository().getBaseURL());
        assertEquals("isssiiaawej", material.getRepositoryIdentifier());
    }
}
