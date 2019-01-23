package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.KeyCompetence;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class KeyCompetenceDaoTest extends DatabaseTestBase {

    @Inject
    private KeyCompetenceDao keyCompetenceDao;

    @Test
    public void findKeyCompetenceByName() {
        KeyCompetence keyCompetence = keyCompetenceDao.findByName("Cultural_and_value_competence");
        assertNotNull(keyCompetence);
        assertSame(1L, keyCompetence.getId());
        assertEquals("Cultural_and_value_competence", keyCompetence.getName());
    }
}
