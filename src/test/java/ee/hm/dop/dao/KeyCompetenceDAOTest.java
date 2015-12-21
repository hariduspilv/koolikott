package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.KeyCompetence;

/**
 * Created by mart on 21.12.15.
 */
public class KeyCompetenceDAOTest extends DatabaseTestBase {

    @Inject
    private KeyCompetenceDAO keyCompetenceDAO;

    @Test
    public void findKeyCompetenceByName() {
        KeyCompetence keyCompetence = keyCompetenceDAO.findKeyCompetenceByName("Cultural_and_value_competence");
        assertNotNull(keyCompetence);
        assertSame(1L, keyCompetence.getId());
        assertEquals("Cultural_and_value_competence", keyCompetence.getName());
    }
}
