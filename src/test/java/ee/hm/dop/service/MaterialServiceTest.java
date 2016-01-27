package ee.hm.dop.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Repository;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.EducationalContext;

@RunWith(EasyMockRunner.class)
public class MaterialServiceTest {

    @TestSubject
    private MaterialService materialService = new MaterialService();

    @Mock
    private MaterialDAO materialDao;

    @Mock
    private SearchEngineService searchEngineService;

    @Test
    public void createMaterialWithNotNullId() {
        Material material = new Material();
        material.setId(123L);

        replay(materialDao);

        try {
            materialService.createMaterial(material, null, false);
            fail("Exception expected.");
        } catch (IllegalArgumentException e) {
            assertEquals("Error creating Material, material already exists.", e.getMessage());
        }

        verify(materialDao);
    }

    @Test
    public void update() {
        DateTime added = new DateTime("2001-10-04T10:15:45.937");
        Long views = 124l;

        Material original = new Material();
        original.setViews(views);
        original.setAdded(added);

        long materialId = 1;
        Material material = createMock(Material.class);
        expect(material.getId()).andReturn(materialId).times(2);
        expect(material.getRepository()).andReturn(null).times(2);
        expect(material.getAuthors()).andReturn(null);
        expect(material.getPublishers()).andReturn(null);
        searchEngineService.updateIndex();

        material.setAdded(added);
        material.setViews(views);

        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setName(MaterialService.BASICEDUCATION);
        expect(material.getTaxons()).andReturn(Arrays.asList(educationalContext)).times(3);

        expect(materialDao.findByIdNotDeleted(materialId)).andReturn(original);
        expect(materialDao.update(material)).andReturn(material);

        replay(materialDao, material, searchEngineService);

        materialService.update(material);

        verify(materialDao, material, searchEngineService);
    }

    @Test
    public void updateWhenMaterialDoesNotExist() {
        long materialId = 1;
        Material material = createMock(Material.class);
        expect(material.getId()).andReturn(materialId);
        expect(materialDao.findByIdNotDeleted(materialId)).andReturn(null);

        replay(materialDao, material);

        try {
            materialService.update(material);
            fail("Exception expected.");
        } catch (IllegalArgumentException ex) {
            assertEquals("Error updating Material: material does not exist.", ex.getMessage());
        }

        verify(materialDao, material);
    }

    @Test
    public void updateAddingRepository() {
        Material original = new Material();

        long materialId = 1;
        Material material = createMock(Material.class);
        expect(material.getId()).andReturn(materialId);
        expect(material.getRepository()).andReturn(new Repository()).times(3);

        expect(materialDao.findByIdNotDeleted(materialId)).andReturn(original);

        replay(materialDao, material);

        try {
            materialService.update(material);
            fail("Exception expected.");
        } catch (IllegalArgumentException ex) {
            assertEquals("Error updating Material: Not allowed to modify repository.", ex.getMessage());
        }

        verify(materialDao, material);
    }

    @Test
    public void updateChangingRepository() {
        Material original = new Material();
        Repository originalRepository = new Repository();
        originalRepository.setBaseURL("original.com");
        original.setRepository(originalRepository);

        long materialId = 1;
        Material material = createMock(Material.class);
        expect(material.getId()).andReturn(materialId);
        Repository newRepository = new Repository();
        newRepository.setBaseURL("some.com");
        expect(material.getRepository()).andReturn(newRepository).times(3);

        expect(materialDao.findByIdNotDeleted(materialId)).andReturn(original);

        replay(materialDao, material);

        try {
            materialService.update(material);
            fail("Exception expected.");
        } catch (IllegalArgumentException ex) {
            assertEquals("Error updating Material: Not allowed to modify repository.", ex.getMessage());
        }

        verify(materialDao, material);
    }

    @Test
    public void delete() {
        Material material = createMock(Material.class);
        materialDao.delete(material);

        replay(materialDao, material);

        materialService.delete(material);

        verify(materialDao, material);
    }

    @Test
    public void updateByUserNullMaterial() {
        User user = createMock(User.class);

        replay(user);

        try {
            materialService.updateByUser(null, user);
            fail("Exception expected.");
        } catch (IllegalArgumentException ex) {
            assertEquals("Material id parameter is mandatory", ex.getMessage());
        }

        verify(user);
    }

    @Test
    public void updateByUserRepoMaterial() {
        User user = createMock(User.class);
        Material material = createMock(Material.class);
        expect(material.getId()).andReturn(1L);
        expect(materialDao.findByIdNotDeleted(1L)).andReturn(material);
        expect(material.getRepository()).andReturn(new Repository());

        replay(material, user, materialDao);

        try {
            materialService.updateByUser(material, user);
            fail("Exception expected.");
        } catch (IllegalArgumentException ex) {
            assertEquals("Can't update external repository material", ex.getMessage());
        }

        verify(material, user, materialDao);
    }

    @Test
    public void updateByUserIsAdmin() throws NoSuchMethodException {
        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);

        expect(materialDao.findByIdNotDeleted(material.getId())).andReturn(material).anyTimes();
        expect(user.getRole()).andReturn(Role.ADMIN).anyTimes();
        expect(materialDao.update(material)).andReturn(new Material());

        replay(user, materialDao);

        Material returned = materialService.updateByUser(material, user);

        assertNotNull(returned);
        verify(user, materialDao);
    }

    @Test
    public void updateByUserIsPublisher() throws NoSuchMethodException {
        User user = createMock(User.class);
        Material material = new Material();
        material.setId(1L);
        material.setRepository(null);
        material.setCreator(user);

        expect(materialDao.findByIdNotDeleted(material.getId())).andReturn(material).anyTimes();
        expect(user.getRole()).andReturn(Role.PUBLISHER).anyTimes();
        expect(materialDao.update(material)).andReturn(new Material());
        expect(user.getUsername()).andReturn("username").anyTimes();

        replay(user, materialDao);

        Material returned = materialService.updateByUser(material, user);

        assertNotNull(returned);
        verify(user, materialDao);
    }
}
