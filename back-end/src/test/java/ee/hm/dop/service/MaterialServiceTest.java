package ee.hm.dop.service;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Publisher;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.EducationalContext;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.IAnswer;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class MaterialServiceTest {

    @TestSubject
    private MaterialService materialService = new MaterialService();

    @Mock
    private MaterialDAO materialDAO;

    @Mock
    private SolrEngineService solrEngineService;

    @Test
    public void create() {
        Capture<Material> capturedMaterial = newCapture();

        Publisher publisher = new Publisher();

        User creator = new User();
        creator.setId(2000L);
        creator.setName("First");
        creator.setSurname("Last");
        creator.setPublisher(publisher);

        Material material = new Material();
        String source = "http://creatematerial.example.com";
        material.setSource(source);
        material.setCurriculumLiterature(true);
        material.setRecommendation(new Recommendation());

        expectMaterialUpdate(capturedMaterial);
        solrEngineService.updateIndex();

        replayAll();

        Material createdMaterial = materialService.createMaterial(material, creator, true);

        verifyAll();

        assertSame(capturedMaterial.getValue(), createdMaterial);
        assertNull(createdMaterial.getRecommendation());
        assertTrue(createdMaterial.isCurriculumLiterature());
        assertEquals(source, createdMaterial.getSource());
    }

    @Test
    public void createMaterialWithNotNullId() {
        Material material = new Material();
        material.setId(123L);

        replay(materialDAO);

        try {
            materialService.createMaterial(material, null, false);
            fail("Exception expected.");
        } catch (IllegalArgumentException e) {
            assertEquals("Error creating Material, material already exists.", e.getMessage());
        }

        verify(materialDAO);
    }

    @Test
    public void update() {
        DateTime startOfTest = now();
        DateTime added = new DateTime("2001-10-04T10:15:45.937");
        Long views = 124l;

        Material original = new Material();
        original.setViews(views);
        original.setAdded(added);

        long materialId = 1;
        Material material = createMock(Material.class);
        expect(material.getId()).andReturn(materialId).times(3);
        expect(material.getAuthors()).andReturn(null);
        expect(material.getPublishers()).andReturn(null);
        material.setRepository(null);
        material.setRecommendation(null);
        solrEngineService.updateIndex();

        material.setAdded(added);
        material.setViews(views);

        Capture<DateTime> capturedUpdateDate = newCapture();
        material.setUpdated(capture(capturedUpdateDate));

        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setName(MaterialService.BASICEDUCATION);
        expect(material.getTaxons()).andReturn(Arrays.asList(educationalContext)).times(3);
        expect(material.getDescriptions()).andReturn(null);

        expect(materialDAO.findByIdNotDeleted(materialId)).andReturn(original);
        expect(materialDAO.update(material)).andReturn(material);

        replay(materialDAO, material, solrEngineService);

        materialService.update(material, null);

        verify(materialDAO, material, solrEngineService);

        DateTime updatedDate = capturedUpdateDate.getValue();
        DateTime maxFuture = now().plusSeconds(20);
        assertTrue(startOfTest.isBefore(updatedDate) || startOfTest.isEqual(updatedDate));
        assertTrue(updatedDate.isBefore(maxFuture));
    }

    @Test
    public void updateWhenMaterialDoesNotExist() {
        long materialId = 1;
        Material material = createMock(Material.class);
        expect(material.getId()).andReturn(materialId).times(2);

        expect(materialDAO.findByIdNotDeleted(materialId)).andReturn(null);

        replay(materialDAO, material);

        try {
            materialService.update(material, null);
            fail("Exception expected.");
        } catch (IllegalArgumentException ex) {
            assertEquals("Error updating Material: material does not exist.", ex.getMessage());
        }

        verify(materialDAO, material);
    }

    @Test
    public void updateAddingRepository() {
        Material original = new Material();
        Repository repository = new Repository();
        original.setRepository(repository);

        long materialId = 1;
        Material material = createMock(Material.class);
        expect(material.getId()).andReturn(materialId).times(3);
        material.setRecommendation(null);
        expect(materialDAO.findByIdNotDeleted(materialId)).andReturn(original);
        material.setRepository(repository);
        expect(materialDAO.update(material)).andReturn(material);
        material.setViews(0L);
        material.setAdded(null);
        material.setUpdated(EasyMock.anyObject(DateTime.class));
        expect(material.getAuthors()).andReturn(null);
        expect(material.getPublishers()).andReturn(null);
        expect(material.getTaxons()).andReturn(null);
        expect(material.getDescriptions()).andReturn(null);
        material.setKeyCompetences(null);
        material.setCrossCurricularThemes(null);

        replay(materialDAO, material);

        Material returned = materialService.update(material, null);

        assertNotNull(returned);
        verify(materialDAO, material);
    }

    @Test
    public void delete() {
        Material material = createMock(Material.class);
        materialDAO.delete(material);

        replay(materialDAO, material);

        materialService.delete(material);

        verify(materialDAO, material);
    }

    @Test
    public void deleteByAdmin() {
        Long materialID = 15L;

        Material originalMaterial = new Material();
        originalMaterial.setId(15L);

        User user = new User();
        user.setRole(Role.ADMIN);

        expect(materialDAO.findByIdNotDeleted(materialID)).andReturn(originalMaterial);
        materialDAO.delete(originalMaterial);
        solrEngineService.updateIndex();

        replayAll();

        materialService.delete(materialID, user);

        verifyAll();
    }

    @Test
    public void adminCanNotDeleteRepositoryMaterial() {
        Long materialID = 15L;

        Material originalMaterial = new Material();
        originalMaterial.setId(materialID);
        originalMaterial.setRepository(new Repository());
        originalMaterial.setRepositoryIdentifier("asd");

        User user = new User();
        user.setRole(Role.ADMIN);

        expect(materialDAO.findByIdNotDeleted(materialID)).andReturn(originalMaterial);

        replayAll();

        try {
            materialService.delete(materialID, user);
        } catch (RuntimeException e) {
            assertEquals("Can not delete external repository material", e.getMessage());
        }

        verifyAll();
    }

    @Test
    public void restore() {
        Long materialID = 15L;

        Material material = new Material();
        material.setId(materialID);

        Material originalMaterial = new Material();
        originalMaterial.setId(15L);

        User user = new User();
        user.setRole(Role.ADMIN);

        expect(materialDAO.findById(materialID)).andReturn(originalMaterial);
        materialDAO.restore(originalMaterial);
        solrEngineService.updateIndex();

        replayAll();

        materialService.restore(material, user);

        verifyAll();
    }

    @Test
    public void adminCanNotRestoreRepositoryMaterial() {
        Long materialID = 15L;

        Material material = new Material();
        material.setId(materialID);

        Material originalMaterial = new Material();
        originalMaterial.setId(materialID);
        originalMaterial.setRepository(new Repository());
        originalMaterial.setRepositoryIdentifier("asd");

        User user = new User();
        user.setRole(Role.ADMIN);

        expect(materialDAO.findById(materialID)).andReturn(originalMaterial);

        replayAll();

        try {
            materialService.restore(material, user);
        } catch (RuntimeException e) {
            assertEquals("Can not restore external repository material", e.getMessage());
        }

        verifyAll();
    }

    @Test
    public void updateByUserNullMaterial() {
        User user = createMock(User.class);

        replay(user);

        try {
            materialService.update(null, user);
            fail("Exception expected.");
        } catch (IllegalArgumentException ex) {
            assertEquals("Material id parameter is mandatory", ex.getMessage());
        }

        verify(user);
    }

    @Test
    public void updateByUserIsAdmin() {
        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);

        expect(materialDAO.findByIdNotDeleted(material.getId())).andReturn(material).anyTimes();
        expect(user.getRole()).andReturn(Role.ADMIN).anyTimes();
        expect(materialDAO.update(material)).andReturn(new Material());

        replay(user, materialDAO);

        Material returned = materialService.update(material, user);

        assertNotNull(returned);
        verify(user, materialDAO);
    }

    @Test
    public void updateByUserIsPublisher() {
        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);
        material.setCreator(user);

        expect(materialDAO.findByIdNotDeleted(material.getId())).andReturn(material).anyTimes();
        expect(user.getRole()).andReturn(Role.USER).anyTimes();
        expect(materialDAO.update(material)).andReturn(new Material());
        expect(user.getUsername()).andReturn("username").anyTimes();

        replay(user, materialDAO);

        Material returned = materialService.update(material, user);

        assertNotNull(returned);
        verify(user, materialDAO);
    }

    @Test
    public void addRecommendation() {
        Capture<Material> capturedMaterial = newCapture();

        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);

        expect(materialDAO.findByIdNotDeleted(material.getId())).andReturn(material).anyTimes();
        expect(user.getRole()).andReturn(Role.ADMIN).anyTimes();
        expectMaterialUpdate(capturedMaterial);
        solrEngineService.updateIndex();

        replayAll(user);

        Recommendation returnedRecommendation = materialService.addRecommendation(material, user);

        verifyAll(user);

        Recommendation recommendation = capturedMaterial.getValue().getRecommendation();
        assertNotNull(recommendation);
        assertEquals(user, recommendation.getCreator());
        assertEquals(recommendation, returnedRecommendation);
    }

    private void expectMaterialUpdate(Capture<Material> capturedMaterial) {
        expect(materialDAO.update(EasyMock.capture(capturedMaterial))).andAnswer(new IAnswer<Material>() {
            @Override
            public Material answer() throws Throwable {
                return capturedMaterial.getValue();
            }
        });
    }

    @Test
    public void removeRecommendation() {
        Capture<Material> capturedMaterial = newCapture();

        Recommendation recommendation = new Recommendation();
        recommendation.setCreator(new User());
        recommendation.setAdded(DateTime.now());

        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);
        material.setRecommendation(recommendation);

        expect(materialDAO.findByIdNotDeleted(material.getId())).andReturn(material).anyTimes();
        expect(user.getRole()).andReturn(Role.ADMIN).anyTimes();
        expectMaterialUpdate(capturedMaterial);
        solrEngineService.updateIndex();

        replayAll(user);

        materialService.removeRecommendation(material, user);

        assertNull(capturedMaterial.getValue().getRecommendation());

        verifyAll(user);
    }

    private void replayAll(Object... mocks) {
        replay(materialDAO, solrEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(materialDAO, solrEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
