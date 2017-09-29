package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.CrossCurricularTheme;
import org.junit.Test;

public class CrossCurricularThemeDaoTest extends DatabaseTestBase {

    @Inject
    private CrossCurricularThemeDao crossCurricularThemeDao;

    @Test
    public void findAll() {
        List<CrossCurricularTheme> result = crossCurricularThemeDao.findAll();

        assertEquals(2, result.size());

        List<String> expected = Arrays.asList("Lifelong_learning_and_career_planning",
                "Environment_and_sustainable_development");
        List<String> actual = result.stream().map(CrossCurricularTheme::getName).collect(Collectors.toList());

        assertTrue(actual.containsAll(expected));
    }


    @Test
    public void findThemeByName() {
        CrossCurricularTheme crossCurricularTheme = crossCurricularThemeDao.findByName("Lifelong_learning_and_career_planning");
        assertNotNull(crossCurricularTheme);
        assertEquals(crossCurricularTheme.getName(), "Lifelong_learning_and_career_planning");
        assertSame(1L, crossCurricularTheme.getId());
    }
}