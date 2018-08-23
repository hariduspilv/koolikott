package ee.hm.dop.service.synchronizer;

import com.google.common.collect.Lists;
import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.dao.ChapterDao;
import ee.hm.dop.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ChapterMigration extends DopDaemonProcess {

    private static final Logger logger = LoggerFactory.getLogger(ChapterMigration.class);
    public static final String NO_TITLE = "See on näidis alampeatükk";

    public void run() {
        try {

            beginTransaction();
            ChapterDao chapterDao = newChapterDao();
            List<Chapter> chapters = chapterDao.chaptersWithPortfolio();
            logger.info(String.format("analyzing %s chapters", chapters.size()));
            int noRowsNoSubChapters = 0;
            int chaptersMigrated = 0;
            int chaptersThatNeedCleanUp = 0;

            for (Chapter chapter : chapters) {
                if (CollectionUtils.isEmpty(chapter.getContentRows()) && CollectionUtils.isEmpty(chapter.getSubchapters()) && StringUtils.isEmpty(chapter.getText())) {
                    noRowsNoSubChapters++;
                    //has subs or content rows, but no chapters
                } else if (CollectionUtils.isEmpty(chapter.getBlocks())) {
                    chaptersMigrated++;
                    String result = "";
                    if (StringUtils.isNotBlank(chapter.getText())) {
                        String text = chapter.getText();
                        if (text.contains("<ol>")) {
                            result += modifyNumbers(text);
                        } else {
                            result += chapter.getText();
                        }
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

            logger.info(String.format("Chapters that didn't need migration: %s, because they have no blocks, rows or subchapters", noRowsNoSubChapters));
            logger.info(String.format("Chapters that were migrated: %s ", chaptersMigrated));
            logger.info(String.format("Chapters that need to cleanup their rows: %s, because they have blocks already", chaptersThatNeedCleanUp));
            closeTransaction();
        } catch (Exception e) {
            logger.info("Chapter migration unexpected error ", e);
        } finally {
            closeEntityManager();
        }

    }

    private static String modifyNumbers(String text) {
        int startIndex = text.indexOf("<ol>");
        int endIndex = text.indexOf("</ol>");
        String before = text.substring(0, startIndex);
        int endValue = endIndex == -1 ? 0 : 5;
        String between = text.substring(startIndex, endIndex + endValue);
        String after = text.substring(endIndex + endValue);
        int i = 1;
        while (between.contains("<li>")) {
            between = between.replaceFirst("<li>", Integer.toString(i++) + ". ");
            between = between.replaceFirst("</li>", "<br/>");
        }
        between = between.replaceFirst("</ol>", "<br/>");
        between = between.replaceFirst("<ol>", "<br/>");
        if (!after.contains("<ol>")) {
            return before + between + after;
        }
        return before + between + modifyNumbers(after);
    }

    private void cleanUp(ChapterDao chapterDao, Chapter chapter) {
        for (Chapter subchapter : chapter.getSubchapters()) {
            deleteContentRows(subchapter, chapterDao);
            chapterDao.deleteChapter(Arrays.asList(subchapter.getId()));
        }
        deleteContentRows(chapter, chapterDao);
        chapterDao.updateChapterText(Arrays.asList(chapter.getId()));
    }

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
        String chapterText;
        if (StringUtils.isBlank(chapter.getText())) {
            chapterText= "";
        } else {
            if (chapter.getText().contains("<ol>")) {
                chapterText = modifyNumbers(chapter.getText());
            } else {
                chapterText = chapter.getText();
            }
        }
        text = text + chapterText;
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
