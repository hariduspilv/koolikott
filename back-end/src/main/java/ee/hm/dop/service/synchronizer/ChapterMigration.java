package ee.hm.dop.service.synchronizer;

import com.google.common.collect.Lists;
import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.dao.ChapterDao;
import ee.hm.dop.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ChapterMigration extends DopDaemonProcess {

    private static final Logger logger = LoggerFactory.getLogger(ChapterMigration.class);
    public static final String NO_TITLE = "See on näidis alampeatükk";

    public void run() {
        try {

            ChapterDao chapterDao = newChapterDao();
            List<Chapter> chapters = chapterDao.chaptersWithPortfolio();
            logger.info(String.format("analyzing %s chapters", chapters.size()));
            int noRowsNoSubChapters = 0;
            int chaptersMigrated = 0;
            int chaptersThatNeedCleanUp = 0;
            beginTransaction();

            for (Chapter chapter : chapters) {
                if (CollectionUtils.isEmpty(chapter.getContentRows()) && CollectionUtils.isEmpty(chapter.getSubchapters()) && StringUtils.isEmpty(chapter.getText())) {
                    noRowsNoSubChapters++;
                    //has subs or content rows, but no chapters
                } else if (CollectionUtils.isEmpty(chapter.getBlocks())) {
                    chaptersMigrated++;
                    String result = "";
                    if (StringUtils.isNotBlank(chapter.getText())) {
                        result += chapter.getText();
                    }
                    if (CollectionUtils.isNotEmpty(chapter.getContentRows())) {
                        result += transformContentRows(chapter);
                    }
                    if (CollectionUtils.isNotEmpty(chapter.getSubchapters())) {
                        result += chapter.getSubchapters().stream().map(ChapterMigration::mapChapterToString).collect(Collectors.joining());
                    }
                    if (StringUtils.isNotBlank(result)) {
                        chapter.setBlocks(Lists.newArrayList(newChapterBlock(result)));
                        chapterDao.createOrUpdate(chapter);
                    }
//                    cleanUp(chapterDao, chapter);
                } else {
                    //has been migrated, needs cleanup
                    chaptersThatNeedCleanUp++;
//                    cleanUp(chapterDao, chapter);
                }
            }

            closeTransaction();
            logger.info(String.format("Chapters that didn't need migration: %s, because they have no blocks, rows or subchapters", noRowsNoSubChapters));
            logger.info(String.format("Chapters that were migrated: %s ", chaptersMigrated));
            logger.info(String.format("Chapters that need to cleanup their rows: %s, because they have blocks already", chaptersThatNeedCleanUp));
        } catch (Exception e) {
            logger.info("Chapter migration unexpected error ", e);
        }

    }

    private void cleanUp(ChapterDao chapterDao, Chapter chapter) {
        for (Chapter subchapter : chapter.getSubchapters()) {
            deleteContentRows(subchapter, chapterDao);
            chapterDao.deleteChapter(Arrays.asList(subchapter.getId()));
        }
        deleteContentRows(chapter, chapterDao);
        chapterDao.updateChapterText(Arrays.asList(chapter.getId()));
    }

    @Override
    public void stop() {

    }

    private void deleteContentRows(Chapter subchapter, ChapterDao chapterDao) {
        if (CollectionUtils.isNotEmpty(subchapter.getContentRows())) {
            List<Long> rowIds2 = subchapter.getContentRows().stream().map(ContentRow::getId).collect(Collectors.toList());
            chapterDao.deleteMaterialRow(rowIds2);
            chapterDao.deleteContentRow(rowIds2);
            chapterDao.deleteChapterRow(rowIds2);
        }
    }

    private static ChapterBlock newChapterBlock(String result) {
        ChapterBlock chapterBlock = new ChapterBlock();
        chapterBlock.setNarrow(false);
        chapterBlock.setHtmlContent(result);
        return chapterBlock;
    }

    private static String mapChapterToString(Chapter chapter) {
        String title = StringUtils.isNotBlank(chapter.getTitle()) ? chapter.getTitle() : NO_TITLE;
        String text = "<h3 class=\"subchapter\">" + title + "</h3>";
        text = text + (StringUtils.isNotBlank(chapter.getText()) ? chapter.getText() : "");
        if (CollectionUtils.isNotEmpty(chapter.getContentRows())) {
            text = text + transformContentRows(chapter);
        }
        return text;
    }

    private static String transformContentRows(Chapter chapter) {
        List<LearningObject> learningObjects = chapter.getContentRows().stream()
                .map(ContentRow::getLearningObjects)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return learningObjects.stream()
                .map(LearningObject::getId)
                .map(Object::toString)
                .map(s -> "<div class=\"chapter-embed-card chapter-embed-card--material\" " +
                        "data-id=\"" + s + "\"></div>")
                .collect(Collectors.joining());
    }

    protected ChapterDao newChapterDao() {
        return GuiceInjector.getInjector().getInstance(ChapterDao.class);
    }
}
