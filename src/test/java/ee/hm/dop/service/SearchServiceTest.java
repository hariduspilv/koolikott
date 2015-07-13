package ee.hm.dop.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.model.Material;

@RunWith(EasyMockRunner.class)
public class SearchServiceTest {

    @Mock
    private SearchEngineService searchEngineService;

    @Mock
    private MaterialDAO materialDAO;

    @TestSubject
    private SearchService searchService = new SearchService();

    @Test
    public void search() {
        String query = "people";
        List<Long> searchResult = new ArrayList<>();
        searchResult.add((long) 7);
        searchResult.add((long) 1);
        searchResult.add((long) 4);

        Material material7 = new Material();
        material7.setId((long) 7);
        Material material1 = new Material();
        material1.setId((long) 1);
        Material material4 = new Material();
        material4.setId((long) 4);

        List<Material> materials = new ArrayList<>();
        materials.add(material1);
        materials.add(material4);
        materials.add(material7);

        expect(searchEngineService.search(query)).andReturn(searchResult);
        expect(materialDAO.findAllById(searchResult)).andReturn(materials);

        replayAll();

        List<Material> result = searchService.search(query);

        verifyAll();

        assertEquals(3, result.size());
        assertSame(material7, result.get(0));
        assertSame(material1, result.get(1));
        assertSame(material4, result.get(2));
    }

    // To test asynchronous problems that may occur when search returns deleted
    // materials
    @Test
    public void searchWhenDatabaseReturnsLessValuesThanSearch() {
        String query = "people";
        List<Long> searchResult = new ArrayList<>();
        searchResult.add((long) 7);
        searchResult.add((long) 1);
        searchResult.add((long) 4);

        Material material7 = new Material();
        material7.setId((long) 7);
        Material material4 = new Material();
        material4.setId((long) 4);

        List<Material> materials = new ArrayList<>();
        materials.add(material4);
        materials.add(material7);

        expect(searchEngineService.search(query)).andReturn(searchResult);
        expect(materialDAO.findAllById(searchResult)).andReturn(materials);

        replayAll();

        List<Material> result = searchService.search(query);

        verifyAll();

        assertEquals(2, result.size());
        assertSame(material7, result.get(0));
        assertSame(material4, result.get(1));
    }

    @Test
    public void searchNoResult() {
        String query = "people";
        List<Long> searchResult = new ArrayList<>();

        expect(searchEngineService.search(query)).andReturn(searchResult);

        replayAll();

        List<Material> result = searchService.search(query);

        verifyAll();

        assertEquals(0, result.size());
    }

    private void replayAll() {
        replay(searchEngineService, materialDAO);
    }

    private void verifyAll() {
        verify(searchEngineService, materialDAO);
    }

}
