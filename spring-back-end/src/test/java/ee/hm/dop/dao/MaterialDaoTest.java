package ee.hm.dop.dao;

import com.google.common.collect.Lists;
import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.common.test.TestLayer;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.enums.LanguageC;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.content.enums.GetMaterialStrategy;

import java.time.LocalDateTime;

import org.junit.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ee.hm.dop.utils.tokenizer.TitleUtils.MAX_TITLE_LENGTH;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MaterialDaoTest extends DatabaseTestBase {

    @Inject
    private MaterialDao materialDao;

    @Test
    public void find() {
        Material material = materialDao.findByIdNotDeleted(MATERIAL_1);
        assertMaterial1(material, TestLayer.DAO);
    }

    @Test
    public void findDeletedMaterial() {
        Material material = materialDao.findByIdNotDeleted(MATERIAL_11);
        assertNull(material);
    }

    @Test
    public void assertMaterial2() {
        Material material = materialDao.findByIdNotDeleted(MATERIAL_2);
        assertEquals(2, material.getAuthors().size());
        assertEquals("Isaac", material.getAuthors().get(0).getName());
        assertEquals("John Newton", material.getAuthors().get(0).getSurname());
        assertEquals("Leonardo", material.getAuthors().get(1).getName());
        assertEquals("Fibonacci", material.getAuthors().get(1).getSurname());

        assertEquals(LanguageC.RUS, material.getLanguage().getCode());
        //todo time
//        assertEquals(LocalDateTime.parse("1995-07-12T09:00:01.000+00:00"), material.getUpdated());

        assertEquals(4, material.getTags().size());
        assertEquals("matemaatika", material.getTags().get(0).getName());
        assertEquals("mathematics", material.getTags().get(1).getName());
        assertEquals("Математика", material.getTags().get(2).getName());
        assertEquals("учебник", material.getTags().get(3).getName());
    }

    @Test
    public void material_TitlesForUrl_returns_title_without_diacritics_and_with_length_of_MAX_TITLE_LENGTH() {
        Material material = materialDao.findByIdNotDeleted(MATERIAL_6);
        assertTrue(material.getTitlesForUrl().get(0).getText().length() <= MAX_TITLE_LENGTH);
    }

    @Test
    public void materialViews() {
        Material material = materialDao.findByIdNotDeleted(MATERIAL_3);
        assertEquals(Long.valueOf(300), material.getViews());
    }

    @Test
    public void findAllById() {
        List<Long> expected = Lists.newArrayList(5L, 7L, 3L);
        List<LearningObject> result = materialDao.findAllById(expected);
        assertNotNull(result);
        List<Long> resultIds = result.stream().map(LearningObject::getId).collect(Collectors.toList());
        assertEquals(3, result.size());
        assertTrue(expected.containsAll(resultIds));
    }

    @Test
    public void findAllByIdNoResult() {
        List<LearningObject> result = materialDao.findAllById(Lists.newArrayList(NOT_EXISTS_ID));
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
    public void createMaterial() {
        Material material = new Material();
        material.setSource("asd");
        material.setAdded(LocalDateTime.now());
        material.setViews((long) 123);
        material.setVisibility(Visibility.PUBLIC);

        Material updated = materialDao.createOrUpdate(material);

        Material newMaterial = materialDao.findByIdNotDeleted(updated.getId());

        assertEquals(material.getSource(), newMaterial.getSource());
        assertEquals(material.getAdded(), newMaterial.getAdded());
        assertEquals(material.getViews(), newMaterial.getViews());
        assertNull(newMaterial.getUpdated());

        materialDao.remove(newMaterial);
    }

    @Test
    public void findMaterialWith2Subjects() {
        Material material = materialDao.findByIdNotDeleted(MATERIAL_6);
        List<Taxon> taxons = material.getTaxons();
        assertNotNull(taxons);
        assertEquals(2, taxons.size());
        Subject biology = (Subject) taxons.get(0);
        assertEquals(Long.valueOf(20), biology.getId());
        assertEquals("Biology", biology.getName());
        Subject math = (Subject) taxons.get(1);
        assertEquals(Long.valueOf(21), math.getId());
        assertEquals("Mathematics", math.getName());
    }

    @Test
    public void findMaterialWithNoTaxon() {
        Material material = materialDao.findByIdNotDeleted(MATERIAL_8);
        List<Taxon> taxons = material.getTaxons();
        assertNotNull(taxons);
        assertEquals(0, taxons.size());
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifier() {
        Material material = materialDao.findByRepository(repository(1L), "isssiiaawej");
        assertMaterial1(material, TestLayer.DAO);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierWhenRepositoryDoesNotExists() {
        Material material = materialDao.findByRepository(repository(10L), "isssiiaawej");
        assertNull(material);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierWhenMaterialIsDeleted() {
        Material material = materialDao.findByRepository(repository(1L), "isssiiaawejdsada4564");
        assertNotNull(material);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierWhenRepositoryIdentifierDoesNotExist() {
        Material material = materialDao.findByRepository(repository(1L), "SomeRandomIdenetifier");
        assertNull(material);
    }

    @Test
    public void findByRepositoryAndrepositoryIdentifierNullRepositoryIdAndNullRepositoryIdentifier() {
        Material material = materialDao.findByRepository(new Repository(), null);
        assertNull(material);
    }

    @Test
    public void update() {
        Material changedMaterial = new Material();
        changedMaterial.setId(MATERIAL_9);
        changedMaterial.setSource("http://www.chaged.it.com");
        LocalDateTime now = LocalDateTime.now();
        changedMaterial.setAdded(now);
        Long views = 234L;
        changedMaterial.setViews(views);
        changedMaterial.setUpdated(now);
        changedMaterial.setVisibility(Visibility.PUBLIC);

        materialDao.createOrUpdate(changedMaterial);

        Material material = materialDao.findByIdNotDeleted(MATERIAL_9);
        assertEquals("http://www.chaged.it.com", changedMaterial.getSource());
        assertEquals(now, changedMaterial.getAdded());
        LocalDateTime updated = changedMaterial.getUpdated();
        assertTrue(updated.isEqual(now) || updated.isAfter(now));
        assertEquals(views, changedMaterial.getViews());

        // Restore to original values
        material.setSource("http://www.chaging.it.com");
        material.setAdded(LocalDateTime.parse("1911-09-01T00:00:01"));
        material.setViews(0L);
        material.setUpdated(null);

        materialDao.createOrUpdate(changedMaterial);
    }

    @Test
    public void delete() {
        Material material = materialDao.findByIdNotDeleted(MATERIAL_10);
        materialDao.delete(material);

        Material deletedMaterial = materialDao.findByIdNotDeleted(MATERIAL_10);
        assertNull(deletedMaterial);
    }

    @Test
    public void isPaidTrue() {
        Material material = materialDao.findByIdNotDeleted(MATERIAL_1);
        assertTrue(material.isPaid());
    }

    @Test
    public void isPaidFalse() {
        Material material = materialDao.findByIdNotDeleted(MATERIAL_9);
        assertFalse(material.isPaid());
    }

    @Test
    public void isEmbeddedWhenNoRepository() {
        Material material = materialDao.findByIdNotDeleted(MATERIAL_3);
        assertFalse(material.isEmbeddable());
    }

    @Test
    public void isEmbeddedWhenEstonianRepo() {
        Material material = materialDao.findByIdNotDeleted(MATERIAL_12);
        assertTrue(material.isEmbeddable());
    }

    @Test
    public void isEmbeddedWhenNotEstonianRepo() {
        Material material = materialDao.findByIdNotDeleted(MATERIAL_1);
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

    public Picture picture() {
        Picture picture = new OriginalPicture();
        picture.setId(1);
        return picture;
    }

    public Repository repository(long id) {
        Repository repository = new Repository();
        repository.setId(id);
        return repository;
    }
}
