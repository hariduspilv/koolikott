package ee.hm.dop.common.test;

import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Recommendation;
import ee.hm.dop.model.enums.TargetGroupEnum;
import ee.hm.dop.model.taxon.Subject;
import org.joda.time.DateTime;

import java.util.List;

import static ee.hm.dop.common.test.TestConstants.PORTFOLIO_1;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Portfolio1Validator {

    public static void assertPortfolio1(Portfolio portfolio, TestLayer layer) {
        assertNotNull(portfolio);
        assertEquals(PORTFOLIO_1, portfolio.getId());
        assertEquals("The new stock market", portfolio.getTitle());
        assertEquals(new DateTime("2000-12-29T08:00:01.000+02:00"), portfolio.getAdded());
        assertEquals(new DateTime("2004-12-29T08:00:01.000+02:00"), portfolio.getUpdated());

        Subject mathematics = (Subject) portfolio.getTaxons().get(0);
        assertEquals("Mathematics", mathematics.getName());
        assertEquals(new Long(21), mathematics.getId());
        assertEquals(2, mathematics.getDomain().getSubjects().size());
        assertEquals(2, mathematics.getDomain().getEducationalContext().getDomains().size());

        assertEquals(new Long(6), portfolio.getCreator().getId());
        assertEquals("mati.maasikas-vaarikas", portfolio.getCreator().getUsername());
        assertEquals(new Long(5), portfolio.getOriginalCreator().getId());
        assertEquals("The changes after 2008.", portfolio.getSummary());
        assertEquals(new Long(95455215), portfolio.getViews());
        assertEquals(5, portfolio.getTags().size());

        List<Chapter> chapters = portfolio.getChapters();
        assertEquals(3, chapters.size());
        Chapter chapter = chapters.get(0);
        assertEquals(new Long(1), chapter.getId());
        assertEquals("The crisis", chapter.getTitle());
        assertNull(chapter.getText());
        List<LearningObject> materials = chapter.getContentRows().get(0).getLearningObjects();
        assertEquals(1, materials.size());
        assertEquals(new Long(1), materials.get(0).getId());
        assertEquals(2, chapter.getSubchapters().size());
        Chapter subchapter1 = chapter.getSubchapters().get(0);
        assertEquals(new Long(4), subchapter1.getId());
        assertEquals("Subprime", subchapter1.getTitle());
        assertNull(subchapter1.getText());
        materials = subchapter1.getContentRows().get(0).getLearningObjects();
        assertEquals(1, materials.size());
        assertEquals(new Long(8), materials.get(0).getId());
        Chapter subchapter2 = chapter.getSubchapters().get(1);
        assertEquals(new Long(5), subchapter2.getId());
        assertEquals("The big crash", subchapter2.getTitle());
        assertEquals("Bla bla bla\nBla bla bla bla bla bla bla", subchapter2.getText());
        materials = subchapter2.getContentRows().get(0).getLearningObjects();
        assertEquals(1, materials.size());
        assertEquals(new Long(3), materials.get(0).getId());

        chapter = chapters.get(1);
        assertEquals(new Long(3), chapter.getId());
        assertEquals("Chapter 2", chapter.getTitle());
        assertEquals("Paragraph 1\n\nParagraph 2\n\nParagraph 3\n\nParagraph 4", chapter.getText());
        assertEquals(1, chapter.getContentRows().get(0).getLearningObjects().size());
        assertEquals(0, chapter.getSubchapters().size());

        chapter = chapters.get(2);
        assertEquals(new Long(2), chapter.getId());
        assertEquals("Chapter 3", chapter.getTitle());
        assertEquals("This is some text that explains what is the Chapter 3 about.\nIt can have many lines\n\n\n"
                + "And can also have    spaces   betwenn    the words on it", chapter.getText());
        assertEquals(1, chapter.getContentRows().get(0).getLearningObjects().size());
        assertEquals(0, chapter.getSubchapters().size());

        assertEquals(2, portfolio.getTargetGroups().size());
        assertTrue(TargetGroupEnum.containsTargetGroup(portfolio.getTargetGroups(), TargetGroupEnum.ZERO_FIVE));
        assertTrue(TargetGroupEnum.containsTargetGroup(portfolio.getTargetGroups(), TargetGroupEnum.SIX_SEVEN));
        assertEquals("Lifelong_learning_and_career_planning", portfolio.getCrossCurricularThemes().get(0).getName());
        assertEquals("Cultural_and_value_competence", portfolio.getKeyCompetences().get(0).getName());
        assertFalse(portfolio.isDeleted());

        Recommendation recommendation = portfolio.getRecommendation();
        assertNotNull(recommendation);
        assertEquals(Long.valueOf(3), recommendation.getId());

        assertEquals(PORTFOLIO_1, portfolio.getId());
        assertEquals("The new stock market", portfolio.getTitle());
        assertEquals(new DateTime("2000-12-29T08:00:01.000+02:00"), portfolio.getAdded());
        assertEquals(new DateTime("2004-12-29T08:00:01.000+02:00"), portfolio.getUpdated());

        assertEquals("Mathematics", mathematics.getName());
        assertEquals(new Long(21), mathematics.getId());
        assertEquals(2, mathematics.getDomain().getSubjects().size());
        assertEquals(2, mathematics.getDomain().getEducationalContext().getDomains().size());

        assertEquals(new Long(6), portfolio.getCreator().getId());
        assertEquals("mati.maasikas-vaarikas", portfolio.getCreator().getUsername());
        assertEquals(new Long(5), portfolio.getOriginalCreator().getId());
        assertEquals("The changes after 2008.", portfolio.getSummary());
        assertEquals(new Long(95455215), portfolio.getViews());
        assertEquals(5, portfolio.getTags().size());

        chapter = chapters.get(0);
        assertEquals(3, chapters.size());
        assertEquals(new Long(1), chapter.getId());
        assertEquals("The crisis", chapter.getTitle());
        assertNull(chapter.getText());
        materials = chapter.getContentRows().get(0).getLearningObjects();
        assertEquals(1, materials.size());
        assertEquals(new Long(1), materials.get(0).getId());
        assertEquals(2, chapter.getSubchapters().size());
        assertEquals(new Long(4), subchapter1.getId());
        assertEquals("Subprime", subchapter1.getTitle());
        assertNull(subchapter1.getText());
        materials = subchapter1.getContentRows().get(0).getLearningObjects();
        assertEquals(1, materials.size());
        assertEquals(new Long(8), materials.get(0).getId());
        assertEquals(new Long(5), subchapter2.getId());
        assertEquals("The big crash", subchapter2.getTitle());
        assertEquals("Bla bla bla\nBla bla bla bla bla bla bla", subchapter2.getText());
        materials = subchapter2.getContentRows().get(0).getLearningObjects();
        assertEquals(1, materials.size());
        assertEquals(new Long(3), materials.get(0).getId());

        chapter = chapters.get(1);
        assertEquals(new Long(3), chapter.getId());
        assertEquals("Chapter 2", chapter.getTitle());
        assertEquals("Paragraph 1\n\nParagraph 2\n\nParagraph 3\n\nParagraph 4", chapter.getText());
        assertEquals(1, chapter.getContentRows().get(0).getLearningObjects().size());
        assertEquals(0, chapter.getSubchapters().size());

        chapter = chapters.get(2);
        assertEquals(new Long(2), chapter.getId());
        assertEquals("Chapter 3", chapter.getTitle());
        assertEquals("This is some text that explains what is the Chapter 3 about.\nIt can have many lines\n\n\n"
                + "And can also have    spaces   betwenn    the words on it", chapter.getText());
        assertEquals(1, chapter.getContentRows().get(0).getLearningObjects().size());
        assertEquals(0, chapter.getSubchapters().size());

        assertEquals(2, portfolio.getTargetGroups().size());
        assertTrue(TargetGroupEnum.containsTargetGroup(portfolio.getTargetGroups(), TargetGroupEnum.ZERO_FIVE));
        assertTrue(TargetGroupEnum.containsTargetGroup(portfolio.getTargetGroups(), TargetGroupEnum.SIX_SEVEN));
        assertEquals("Lifelong_learning_and_career_planning", portfolio.getCrossCurricularThemes().get(0).getName());
        assertEquals("Cultural_and_value_competence", portfolio.getKeyCompetences().get(0).getName());
        assertFalse(portfolio.isDeleted());

        assertNotNull(recommendation);
        assertEquals(Long.valueOf(3), recommendation.getId());
    }
}
