package ee.hm.dop.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.LanguageDAO;
import ee.hm.dop.dao.PageDAO;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.Page;

@RunWith(EasyMockRunner.class)
public class PageServiceTest {

    @TestSubject
    private PageService pageService = new PageService();

    @Mock
    private LanguageDAO languageDao;

    @Mock
    private PageDAO pageDao;

    @Test
    public void getPageForNull() {

        replayAll();

        assertNull(pageService.getPage(null, null));

        verifyAll();
    }

    @Test
    public void getPageForNotSupportedLanguage() {
        String name = "existingPage";
        Language language = null;

        replayAll();

        assertNull(pageService.getPage(name, language));

        verifyAll();
    }

    @Test
    public void getPageForSupportedLanguageButNoPage() {
        String name = "doesnotExist";
        Language language = createMock(Language.class);

        expect(pageDao.findByNameAndLang(name, language)).andReturn(null);

        replayAll(language);

        assertNull(pageService.getPage(name, language));

        verifyAll(language);
    }

    @Test
    public void getPageForSupportedLanguage() {
        String name = "existingPage";
        Language language = createMock(Language.class);

        Page page = createMock(Page.class);
        Language pageLanguage = createMock(Language.class);

        expect(pageDao.findByNameAndLang(name, language)).andReturn(page).times(2);
        expect(page.getLanguage()).andReturn(pageLanguage);

        replayAll(language, page, pageLanguage);

        assertSame(page, pageService.getPage(name, language));
        assertSame(pageLanguage, pageService.getPage(name, language).getLanguage());

        verifyAll(language, page, pageLanguage);
    }

    private void replayAll(Object... mocks) {
        replay(languageDao, pageDao);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(languageDao, pageDao);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

}
