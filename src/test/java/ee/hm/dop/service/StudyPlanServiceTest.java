package ee.hm.dop.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertSame;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.StudyPlanDAO;
import ee.hm.dop.model.StudyPlan;

@RunWith(EasyMockRunner.class)
public class StudyPlanServiceTest {

    @TestSubject
    private StudyPlanService studyPlanService = new StudyPlanService();

    @Mock
    private StudyPlanDAO studyPlanDAO;

    @Test
    public void get() {
        int studyPlanId = 125;
        StudyPlan studyPlan = createMock(StudyPlan.class);
        expect(studyPlanDAO.findById(studyPlanId)).andReturn(studyPlan);

        replayAll(studyPlan);

        StudyPlan result = studyPlanService.get(studyPlanId);

        verifyAll(studyPlan);

        assertSame(studyPlan, result);
    }

    private void replayAll(Object... mocks) {
        replay(studyPlanDAO);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(studyPlanDAO);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

}
