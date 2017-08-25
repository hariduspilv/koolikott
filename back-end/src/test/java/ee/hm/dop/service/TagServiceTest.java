package ee.hm.dop.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import ee.hm.dop.dao.TagDao;
import ee.hm.dop.model.Tag;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class TagServiceTest {

    @TestSubject
    private TagService tagService = new TagService();

    @Mock
    private TagDao tagDao;

    @Test
    public void get() {
        String name = "tere";
        Tag tag = new Tag();
        tag.setId(123L);
        tag.setName(name);

        expect(tagDao.findByName(name)).andReturn(tag);

        replay(tagDao);

        Tag result = tagService.getTagByName(name);

        verify(tagDao);

        assertEquals(tag, result);
    }

}
