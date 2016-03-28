package ee.hm.dop.dao;

import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Searchable;

public class UserLikeDAOTest extends DatabaseTestBase {

    @Inject
    private UserLikeDAO userLikeDAO;

    @Test
    public void findMostLikedSince() {
        List<Searchable> objects = userLikeDAO.findMostLikedSince(now().minusDays(100), 5);
        assertEquals(3, objects.size());

        assertEquals(new Long(103), objects.get(0).getId());
        assertEquals(new Long(1), objects.get(1).getId());
        assertEquals(new Long(2), objects.get(2).getId());
    }

    @Test
    public void findMostLikedSinceFuture() {
        List<Searchable> objects = userLikeDAO.findMostLikedSince(now().plusDays(1), 5);
        assertTrue(objects.isEmpty());
    }
}
