package ee.hm.dop.dao;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.RollbackException;

import com.google.common.collect.Lists;
import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.common.test.TestConstants;
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.LanguageC;
import ee.hm.dop.model.enums.TargetGroupEnum;
=======
import ee.hm.dop.common.test.TestLayer;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.LanguageC;
import ee.hm.dop.model.enums.TargetGroupEnum;
import ee.hm.dop.model.enums.Visibility;
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.content.enums.GetMaterialStrategy;
import ee.hm.dop.utils.DbUtils;
import org.joda.time.DateTime;
import org.junit.Test;

public class MaterialDaoTest extends DatabaseTestBase {

    @Inject
    private MaterialDao materialDao;

    @Test
    public void find() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
        assertMaterial1(material);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_1);
        assertMaterial1(material, TestLayer.DAO);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
    }

    @Test
    public void findDeletedMaterial() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_11);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_11);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertNull(material);
    }

    @Test
    public void authors() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_2);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_2);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertEquals(2, material.getAuthors().size());
        assertEquals("Isaac", material.getAuthors().get(0).getName());
        assertEquals("John Newton", material.getAuthors().get(0).getSurname());
        assertEquals("Leonardo", material.getAuthors().get(1).getName());
        assertEquals("Fibonacci", material.getAuthors().get(1).getSurname());
    }

    @Test
    public void materialLanguage() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material1 = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_2);
        assertEquals(LanguageC.RUS, material1.getLanguage().getCode());

        Material material2 = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
=======
        Material material1 = materialDao.findByIdNotDeleted(MATERIAL_2);
        assertEquals(LanguageC.RUS, material1.getLanguage().getCode());

        Material material2 = materialDao.findByIdNotDeleted(MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertEquals(LanguageC.EST, material2.getLanguage().getCode());
    }

    @Test
    public void materialResourceType() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material1 = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
        assertEquals("TEXTBOOK1", material1.getResourceTypes().get(0).getName());

        Material material2 = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
=======
        Material material1 = materialDao.findByIdNotDeleted(MATERIAL_1);
        assertEquals("TEXTBOOK1", material1.getResourceTypes().get(0).getName());

        Material material2 = materialDao.findByIdNotDeleted(MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertEquals("EXPERIMENT1", material2.getResourceTypes().get(1).getName());
    }

    @Test
    public void materialTaxon() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material1 = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
=======
        Material material1 = materialDao.findByIdNotDeleted(MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertEquals("BASICEDUCATION", material1.getTaxons().get(0).getName());
        assertEquals("Biology", material1.getTaxons().get(1).getName());
    }

    @Test
    public void materialLicense() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertEquals("CCBY", material.getLicenseType().getName());
    }

    @Test
    public void materialPublisher() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertEquals("Koolibri", material.getPublishers().get(0).getName());
        assertEquals("http://www.pegasus.ee", material.getPublishers().get(1).getWebsite());
    }

    @Test
    public void materialViews() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_3);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_3);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertEquals(Long.valueOf(300), material.getViews());
    }

    @Test
    public void findAllById() {
        List<Long> expected = Lists.newArrayList(5L, 7L, 3L);
        List<Long> unexpected = Lists.newArrayList(11L);

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        List<Long> expectedIdList = new ArrayList<>(idList);
        idList.add((long) 11); // deleted, should not return

        List<LearningObject> result = materialDao.findAllById(idList);
=======
        List<LearningObject> result = materialDao.findAllById(expected);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java

        assertNotNull(result);
        assertEquals(3, result.size());

        assertTrue(result.stream().noneMatch(m -> unexpected.contains(m.getId())));
    }

    @Test
    public void findAllByIdNoResult() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        List<Long> idList = new ArrayList<>();
        idList.add((long) 1155);

        List<LearningObject> result = materialDao.findAllById(idList);

=======
        List<Long> idList = Lists.newArrayList(1155L);
        List<LearningObject> result = materialDao.findAllById(idList);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void findAllByIdEmptyList() {
        List<LearningObject> result = materialDao.findAllById(new ArrayList<>());
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java

=======
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void materialAddedDate() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertEquals(new DateTime("1999-01-01T02:00:01.000+02:00"), material.getAdded());
    }

    @Test
    public void materialUpdatedDate() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_2);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_2);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertEquals(new DateTime("1995-07-12T09:00:01.000+00:00"), material.getUpdated());
    }

    @Test
    public void materialTags() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_2);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_2);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java

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
        material.setVisibility(Visibility.PUBLIC);

        material.setPicture(picture());

        Material updated = materialDao.createOrUpdate(material);

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
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_6);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_6);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
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
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_8);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_8);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        List<Taxon> taxons = material.getTaxons();
        assertNotNull(taxons);
        assertEquals(0, taxons.size());
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifier() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Repository repository = new Repository();
        repository.setId(1L);

        Material material = materialDao.findByRepositoryAndRepositoryIdentifier(repository, "isssiiaawej");
        assertMaterial1(material);
=======
        Material material = materialDao.findByRepository(repository(1L), "isssiiaawej");
        assertMaterial1(material, TestLayer.DAO);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierWhenRepositoryDoesNotExists() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Repository repository = new Repository();
        repository.setId(10L);

        Material material = materialDao.findByRepositoryAndRepositoryIdentifier(repository, "isssiiaawej");
=======
        Material material = materialDao.findByRepository(repository(10L), "isssiiaawej");
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertNull(material);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierWhenMaterialIsDeleted() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Repository repository = new Repository();
        repository.setId(1L);

        Material material = materialDao.findByRepositoryAndRepositoryIdentifier(repository, "isssiiaawejdsada4564");
=======
        Material material = materialDao.findByRepository(repository(1L), "isssiiaawejdsada4564");
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertNotNull(material);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierWhenRepositoryIdentifierDoesNotExist() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Repository repository = new Repository();
        repository.setId(1L);

        Material material = materialDao.findByRepositoryAndRepositoryIdentifier(repository, "SomeRandomIdenetifier");
=======
        Material material = materialDao.findByRepository(repository(1L), "SomeRandomIdenetifier");
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertNull(material);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierNullRepositoryIdAndNullRepositoryIdentifier() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Repository repository = new Repository();

        Material material = materialDao.findByRepositoryAndRepositoryIdentifier(repository, null);
=======
        Material material = materialDao.findByRepository(new Repository(), null);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertNull(material);
    }

    @Test
    public void findByCreator() {
        List<LearningObject> materials = materialDao.findByCreator(userWithId(1L), 0, Integer.MAX_VALUE);
        List<Long> collect = materials.stream().map(Searchable::getId).collect(Collectors.toList());
        assertTrue(collect.containsAll(asList(MATERIAL_8, MATERIAL_4, MATERIAL_1)));

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        List<LearningObject> materials = materialDao.findByCreator(creator, 0, Integer.MAX_VALUE);
        List<Long> collect = materials.stream().map(Searchable::getId).collect(Collectors.toList());
        assertTrue(collect.containsAll(asList(TestConstants.MATERIAL_8, TestConstants.MATERIAL_4, TestConstants.MATERIAL_1)));

        assertMaterial1((Material) materials.stream().filter(m -> m.getId().equals(TestConstants.MATERIAL_1)).findAny().orElseThrow(RuntimeException::new));
=======
        assertMaterial1((Material) materials.stream().filter(m -> m.getId().equals(MATERIAL_1)).findAny().orElseThrow(RuntimeException::new), TestLayer.DAO);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
    }

    @Test
    public void update() {
        Material changedMaterial = new Material();
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        changedMaterial.setId(TestConstants.MATERIAL_9);
=======
        changedMaterial.setId(MATERIAL_9);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        changedMaterial.setSource("http://www.chaged.it.com");
        DateTime now = new DateTime();
        changedMaterial.setAdded(now);
        Long views = 234L;
        changedMaterial.setViews(views);
        changedMaterial.setUpdated(now);
        changedMaterial.setVisibility(Visibility.PUBLIC);

        materialDao.createOrUpdate(changedMaterial);

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_9);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_9);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertEquals("http://www.chaged.it.com", changedMaterial.getSource());
        assertEquals(now, changedMaterial.getAdded());
        DateTime updated = changedMaterial.getUpdated();
        assertTrue(updated.isEqual(now) || updated.isAfter(now));
        assertEquals(views, changedMaterial.getViews());

        // Restore to original values
        material.setSource("http://www.chaging.it.com");
        material.setAdded(new DateTime("1911-09-01T00:00:01"));
        material.setViews(0L);
        material.setUpdated(null);

        materialDao.createOrUpdate(changedMaterial);
    }

    @Test
    public void updateCreatingNewLanguage() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material originalMaterial = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
=======
        Material originalMaterial = materialDao.findByIdNotDeleted(MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java

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
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material originalMaterial = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
=======
        Material originalMaterial = materialDao.findByIdNotDeleted(MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java

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
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material originalMaterial = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
=======
        Material originalMaterial = materialDao.findByIdNotDeleted(MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java

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
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material originalMaterial = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
=======
        Material originalMaterial = materialDao.findByIdNotDeleted(MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java

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
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material originalMaterial = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
=======
        Material originalMaterial = materialDao.findByIdNotDeleted(MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java

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
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_10);
        materialDao.delete(material);

        Material deletedMaterial = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_10);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_10);
        materialDao.delete(material);

        Material deletedMaterial = materialDao.findByIdNotDeleted(MATERIAL_10);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
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
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertTrue(material.isPaid());
    }

    @Test
    public void isPaidFalse() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_9);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_9);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertFalse(material.isPaid());

    }

    @Test
    public void isEmbeddedWhenNoRepository() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_3);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_3);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertFalse(material.isEmbeddable());
    }

    @Test
    public void isEmbeddedWhenEstonianRepo() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_12);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_12);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertTrue(material.isEmbeddable());
    }

    @Test
    public void isEmbeddedWhenNotEstonianRepo() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        Material material = materialDao.findByIdNotDeleted(TestConstants.MATERIAL_1);
=======
        Material material = materialDao.findByIdNotDeleted(MATERIAL_1);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
        assertFalse(material.isEmbeddable());
    }

    @Test
    public void getMaterialsBySource1() {
        List<Material> materials = materialDao.findBySource("en.wikipedia.org/wiki/Power_Architecture", GetMaterialStrategy.ONLY_EXISTING);
        assertEquals(2, materials.size());
    }

    @Test
    public void getMaterialsBySource2() {
        List<Material> materials = materialDao.findBySource("youtube.com/watch?v=gSWbx3CvVUk", GetMaterialStrategy.ONLY_EXISTING);
        assertEquals(1, materials.size());
    }

    @Test
    public void getMaterialsBySource3() {
        List<Material> materials = materialDao.findBySource("www.youtube.com/watch?v=gSWbx3CvVUk", GetMaterialStrategy.ONLY_EXISTING);
        assertEquals(1, materials.size());
    }

    @Test
    public void getMaterialsBySource4() {
        List<Material> materials = materialDao.findBySource("https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes", GetMaterialStrategy.ONLY_EXISTING);
        assertEquals(1, materials.size());
    }

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java
    private void assertMaterial1(Material material) {
        assertEquals(2, material.getTitles().size());
        assertEquals("Matemaatika õpik üheksandale klassile", material.getTitles().get(0).getText());
        assertEquals(2, material.getDescriptions().size());
        assertEquals("Test description in estonian. (Russian available)", material.getDescriptions().get(0).getText());
        Language descriptionLanguage = material.getDescriptions().get(0).getLanguage();
        assertEquals(LanguageC.EST, descriptionLanguage.getCode());
        assertEquals("Estonian", descriptionLanguage.getName());
        Language language = material.getLanguage();
        assertNotNull(language);
        assertEquals(LanguageC.EST, language.getCode());
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
=======
    public Picture picture() {
        Picture picture = new OriginalPicture();
        picture.setId(1);
        return picture;
    }
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/MaterialDaoTest.java

    public Repository repository(long id) {
        Repository repository = new Repository();
        repository.setId(id);
        return repository;
    }

}
