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

import ee.hm.dop.dao.TaxonDAO;
import ee.hm.dop.model.EducationalContext;

@RunWith(EasyMockRunner.class)
public class TaxonServiceTest {

    @TestSubject
    private TaxonService taxonService = new TaxonService();

    @Mock
    private TaxonDAO taxonDAO;

    @Test
    public void getEducationalContextByName() {
        String name = "preschool";
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setId(123L);
        educationalContext.setName(name);

        expect(taxonDAO.findEducationalContextByName(name)).andReturn(educationalContext);

        replay(taxonDAO);

        EducationalContext result = taxonService.getEducationalContextByName(name);

        verify(taxonDAO);

        assertEquals(educationalContext, result);
    }

}
