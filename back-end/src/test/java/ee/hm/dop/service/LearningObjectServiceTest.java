package ee.hm.dop.service;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ee.hm.dop.dao.LearningObjectDAO;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Visibility;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class LearningObjectServiceTest {

    @TestSubject
    private LearningObjectService learningObjectService;

    @Mock
    private LearningObjectDAO learningObjectDAO;

    @Mock
    private SolrEngineService solrEngineService;

    public LearningObjectServiceTest() throws NoSuchMethodException {
        learningObjectService = getLearningObjectService();
    }

    @Test
    public void getNewestLearningObjects() throws NoSuchMethodException {
        int numberOfLearningObjects = 4;

        Portfolio portfolio1 = new Portfolio();
        portfolio1.setVisibility(Visibility.PUBLIC);

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setVisibility(Visibility.PRIVATE);

        Material material1 = new Material();

        Portfolio portfolio3 = new Portfolio();
        portfolio3.setVisibility(Visibility.PUBLIC);

        // Needs to be modifiable
        List<LearningObject> firstLearningObjects = new ArrayList<>(
                Arrays.asList(portfolio1, portfolio2, material1, portfolio3));
        expect(learningObjectService.getLearningObjectDAO()).andReturn(learningObjectDAO).anyTimes();
        expect(learningObjectDAO.findNewestLearningObjects(numberOfLearningObjects, 0)).andReturn(firstLearningObjects);

        expect(learningObjectService.getLearningObjectHandler(portfolio1)).andReturn(new PortfolioService()).anyTimes();
        expect(learningObjectService.getLearningObjectHandler(portfolio2)).andReturn(new PortfolioService()).anyTimes();
        expect(learningObjectService.getLearningObjectHandler(portfolio3)).andReturn(new PortfolioService()).anyTimes();
        expect(learningObjectService.getLearningObjectHandler(material1)).andReturn(new MaterialService()).anyTimes();

        Portfolio portfolio4 = new Portfolio();
        portfolio4.setVisibility(Visibility.PUBLIC);

        List<LearningObject> secondLearningObjects = new ArrayList<>();
        secondLearningObjects.add(portfolio4);
        expect(learningObjectDAO.findNewestLearningObjects(1, 4)).andReturn(secondLearningObjects);
        expect(learningObjectService.getLearningObjectHandler(portfolio4)).andReturn(new PortfolioService()).anyTimes();

        List<LearningObject> expected = Arrays.asList(portfolio1, material1, portfolio3, portfolio4);

        replayAll(learningObjectService);

        List<LearningObject> result = learningObjectService.getNewestLearningObjects(numberOfLearningObjects);

        verifyAll(learningObjectService);

        assertEquals(expected.size(), result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    public void getNewestLearningObjectsWhenNoResults() {
        int numberOfLearningObjects = 4;

        expect(learningObjectService.getLearningObjectDAO()).andReturn(learningObjectDAO).anyTimes();
        expect(learningObjectDAO.findNewestLearningObjects(numberOfLearningObjects, 0)).andReturn(new ArrayList<>());
        List<LearningObject> expected = Arrays.asList();

        replayAll(learningObjectService);

        List<LearningObject> result = learningObjectService.getNewestLearningObjects(numberOfLearningObjects);

        verifyAll(learningObjectService);

        assertEquals(expected.size(), result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    public void getNewestLearningObjectsWhenNotEnoughResults() {
        int numberOfLearningObjects = 4;

        Portfolio portfolio1 = new Portfolio();
        portfolio1.setVisibility(Visibility.PUBLIC);

        List<LearningObject> firstLearningObjects = new ArrayList<>();
        firstLearningObjects.add(portfolio1);

        expect(learningObjectService.getLearningObjectDAO()).andReturn(learningObjectDAO).anyTimes();
        expect(learningObjectDAO.findNewestLearningObjects(numberOfLearningObjects, 0)).andReturn(firstLearningObjects);
        expect(learningObjectService.getLearningObjectHandler(portfolio1)).andReturn(new PortfolioService()).anyTimes();

        expect(learningObjectDAO.findNewestLearningObjects(3, 4)).andReturn(new ArrayList<>());

        List<LearningObject> expected = Collections.singletonList(portfolio1);

        replayAll(learningObjectService);

        List<LearningObject> result = learningObjectService.getNewestLearningObjects(numberOfLearningObjects);

        verifyAll(learningObjectService);

        assertEquals(expected.size(), result.size());
        assertTrue(result.containsAll(expected));
    }

    private void replayAll(Object... mocks) {
        replay(learningObjectDAO, solrEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(learningObjectDAO, solrEngineService);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

    public LearningObjectService getLearningObjectService() throws NoSuchMethodException {
        Method getLearningObjectHandler = LearningObjectService.class.getDeclaredMethod("getLearningObjectHandler", LearningObject.class);
        Method getLearningObjectDAO = LearningObjectService.class.getDeclaredMethod("getLearningObjectDAO");

        return createMockBuilder(LearningObjectService.class).addMockedMethods(getLearningObjectHandler, getLearningObjectDAO).createMock();
    }
}
