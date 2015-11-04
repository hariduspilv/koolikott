package ee.hm.dop.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.EducationalContextDAO;
import ee.hm.dop.model.EducationalContext;

@RunWith(EasyMockRunner.class)
public class EducationalContextServiceTest {

    @TestSubject
    private EducationalContextService educationalContextService = new EducationalContextService();

    @Mock
    private EducationalContextDAO educationalContextDAO;

    @Test
    public void get() {
        String name = "preschool";
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(123L);
        educationalContext.setName(name);

        expect(educationalContextDAO.findEducationalContextByName(name)).andReturn(educationalContext);

        replay(educationalContextDAO);

        EducationalContext result = educationalContextService.getEducationalContextByName(name);

        verify(educationalContextDAO);

        assertEquals(educationalContext, result);
    }

}
