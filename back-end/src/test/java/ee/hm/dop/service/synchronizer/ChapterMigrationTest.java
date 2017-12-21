package ee.hm.dop.service.synchronizer;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.ChapterDao;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.ChapterBlock;
import ee.hm.dop.model.ContentRow;
import ee.hm.dop.model.Material;
import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

@Ignore
@RunWith(EasyMockRunner.class)
public class ChapterMigrationTest {

    public static final String CHAPTER_TITLE = "Chapter_Title";
    public static final String CHAPTER_TEXT = "Chapter_Text";
    @TestSubject
    private ChapterMigration chapterMigration = new ChapterMigrationTest.ChapterMigrationMock();
    @Mock
    private ChapterDao chapterDao;
    @Mock
    private Configuration configuration;

    @Test
    public void chapter_with_title_only_is_the_same() throws Exception {
        Chapter chapter = new Chapter();
        chapter.setTitle(CHAPTER_TITLE);

        expect(chapterDao.chaptersWithPortfolio()).andReturn(Arrays.asList(chapter));

        run();

        assertEquals(CHAPTER_TITLE, chapter.getTitle());
        assertTrue(isEmpty(chapter.getBlocks()));
    }

    @Test
    public void chapter_with_title_and_text_has_text_migrated() throws Exception {
        Chapter chapter = subChapter();
        chapter.setContentRows(new ArrayList<>());

        expect(chapterDao.chaptersWithPortfolio()).andReturn(Arrays.asList(chapter));
        expect(chapterDao.createOrUpdate(chapter)).andReturn(chapter);

        run();

        assertTheresAreBlocks(chapter);
        validate1Chapter(CHAPTER_TEXT, chapter);
    }

    private void run() {
        replay(chapterDao, configuration);
        chapterMigration.run();
        verify(chapterDao, configuration);
    }

    @Test
    public void chapter_with_LO_has_LO_migrated() throws Exception {
        Chapter chapter = new Chapter();
        chapter.setTitle(CHAPTER_TITLE);
        chapter.setContentRows(mockRows());

        expect(chapterDao.chaptersWithPortfolio()).andReturn(Arrays.asList(chapter));
        expect(chapterDao.createOrUpdate(chapter)).andReturn(chapter);

        run();

        assertTheresAreBlocks(chapter);
        validate1Chapter("<div class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"1\"></div>", chapter);
    }

    @Test
    public void chapter_with_subChapters_has_subChapters_migrated() throws Exception {
        Chapter chapter = new Chapter();
        chapter.setTitle(CHAPTER_TITLE);
        chapter.setContentRows(new ArrayList<>());
        chapter.setSubchapters(Arrays.asList(subChapter()));

        expect(chapterDao.chaptersWithPortfolio()).andReturn(Arrays.asList(chapter));
        expect(chapterDao.createOrUpdate(chapter)).andReturn(chapter);

        run();

        assertTheresAreBlocks(chapter);
        validate1Chapter("<h3 class=\"subchapter\">Chapter_Title</h3>Chapter_Text", chapter);
    }

    @Test
    public void chapter_with_many_subChapters_has_many_subChapters_migrated() throws Exception {
        Chapter chapter = new Chapter();
        chapter.setTitle(CHAPTER_TITLE);
        chapter.setContentRows(new ArrayList<>());
        chapter.setSubchapters(Arrays.asList(subChapter(), subChapter()));

        expect(chapterDao.chaptersWithPortfolio()).andReturn(Arrays.asList(chapter));
        expect(chapterDao.createOrUpdate(chapter)).andReturn(chapter);

        run();

        assertTheresAreBlocks(chapter);
        validate1Chapter("<h3 class=\"subchapter\">Chapter_Title</h3>Chapter_Text<h3 class=\"subchapter\">Chapter_Title</h3>Chapter_Text", chapter);
    }

    @Test
    public void chapter_with_subChapters_w_LOs_has_subChapters_w_LOs_migrated() throws Exception {
        Chapter chapter = new Chapter();
        chapter.setTitle(CHAPTER_TITLE);
        chapter.setContentRows(new ArrayList<>());
        Chapter chapter2 = subChapter();
        chapter2.setContentRows(mockRows());
        chapter.setSubchapters(Lists.newArrayList(chapter2));

        expect(chapterDao.chaptersWithPortfolio()).andReturn(Arrays.asList(chapter));
        expect(chapterDao.createOrUpdate(chapter)).andReturn(chapter);

        run();

        assertTheresAreBlocks(chapter);
        validate1Chapter("<h3 class=\"subchapter\">Chapter_Title</h3>Chapter_Text<div class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"1\"></div>", chapter);
    }


    @Test
    public void chapter_with_subChapters_w_LOs_has_subChapters_w_LOs_migrated_no_sub_title() throws Exception {
        Chapter chapter = new Chapter();
        chapter.setTitle(CHAPTER_TITLE);
        chapter.setContentRows(new ArrayList<>());
        Chapter chapter2 = new Chapter();
        chapter2.setText(CHAPTER_TEXT);
        chapter2.setContentRows(mockRows());
        chapter.setSubchapters(Lists.newArrayList(chapter2));

        expect(chapterDao.chaptersWithPortfolio()).andReturn(Arrays.asList(chapter));
        expect(chapterDao.createOrUpdate(chapter)).andReturn(chapter);

        run();

        assertTheresAreBlocks(chapter);
        validate1Chapter("<h3 class=\"subchapter\">See on näidis alampeatükk</h3>Chapter_Text<div class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"1\"></div>", chapter);
    }

    @Test
    public void chapter_with_subChapters_w_LOs_and_LOs_has_everything_migrated() throws Exception {
        Chapter chapter = new Chapter();
        chapter.setTitle(CHAPTER_TITLE);
        chapter.setContentRows(mockRows());
        Chapter chapter2 = new Chapter();
        chapter2.setContentRows(mockRows());
        chapter.setSubchapters(Lists.newArrayList(chapter2));

        expect(chapterDao.chaptersWithPortfolio()).andReturn(Arrays.asList(chapter));
        expect(chapterDao.createOrUpdate(chapter)).andReturn(chapter);

        run();

        assertTheresAreBlocks(chapter);
        validate1Chapter("<div class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"1\"></div><h3 class=\"subchapter\">See on näidis alampeatükk</h3><div class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"1\"></div>", chapter);

        assertTrue(isNotEmpty(chapter.getContentRows()));
        assertTrue(isNotEmpty(chapter.getSubchapters()));
    }

    private Chapter subChapter() {
        Chapter chapter2 = new Chapter();
        chapter2.setTitle(CHAPTER_TITLE);
        chapter2.setText(CHAPTER_TEXT);
        return chapter2;
    }

    private void assertTheresAreBlocks(Chapter chapter) {
        assertEquals(CHAPTER_TITLE, chapter.getTitle());
        assertTrue(isNotEmpty(chapter.getBlocks()));
        assertEquals(1L, chapter.getBlocks().size());
    }

    private void validate1Chapter(String expectedText, Chapter chapter) {
        ChapterBlock block = chapter.getBlocks().get(0);
        assertFalse(block.isNarrow());
        assertEquals(expectedText, block.getHtmlContent());
    }

    private static ArrayList<ContentRow> mockRows() {
        ContentRow contentRow = new ContentRow();
        Material learningObject = new Material();
        learningObject.setId(1L);
        contentRow.setLearningObjects(Lists.newArrayList(learningObject));
        return Lists.newArrayList(contentRow);
    }

    private class ChapterMigrationMock extends ChapterMigration {

        @Override
        protected ChapterDao newChapterDao() {
            return chapterDao;
        }

        @Override
        public long getInitialDelay(int hourOfDayToExecute) {
            return 1;
        }

        @Override
        protected void beginTransaction() {
        }

        @Override
        protected void closeTransaction() {
        }
    }
}
