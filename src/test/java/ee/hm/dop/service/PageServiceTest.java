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

import ee.hm.dop.dao.PageDAO;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.Page;

@RunWith(EasyMockRunner.class)
public class PageServiceTest {

    @TestSubject
    private PageService pageService = new PageService();

    @Mock
    private PageDAO pageDao;

    @Test
    public void getPageForSupportedLanguage() {
        String name = "existingPage";
        Language language = createMock(Language.class);

        Page page = createMock(Page.class);
        expect(pageDao.findByNameAndLanguage(name, language)).andReturn(page);

        replayAll(language, page);

        Page result = pageService.getPage(name, language);

        verifyAll(language, page);

        assertSame(page, result);
    }

    private void replayAll(Object... mocks) {
        replay(pageDao);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(pageDao);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

}
