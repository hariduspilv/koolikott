package ee.hm.dop.dao;

import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Searchable;

public class UserLikeDAOTest extends DatabaseTestBase {

    @Inject
    private UserLikeDAO userLikeDAO;

    @Test
    public void findMostLikedSince() {
        List<Searchable> objects = userLikeDAO.findMostLikedSince(now().minusDays(100), 5);
        assertEquals(4, objects.size());

        assertEquals(4, getScore(objects.get(0)));
        assertEquals(3, getScore(objects.get(1)));
        assertEquals(2, getScore(objects.get(2)));
        assertEquals(-2, getScore(objects.get(3)));
    }

    @Test
    public void findMostLikedSinceFuture() {
        List<Searchable> objects = userLikeDAO.findMostLikedSince(now().plusDays(1), 5);
        assertTrue(objects.isEmpty());
    }

    private int getScore(Object object) {
        if (object instanceof Material) {
            return getScore((Material) object);
        } else if (object instanceof Portfolio) {
            return getScore((Portfolio) object);
        }

        throw new RuntimeException("Object must be Material or Portfolio");
    }

    private int getScore(Material material) {
        return material.getLikes() - material.getDislikes();
    }

    private int getScore(Portfolio portfolio) {
        return portfolio.getLikes() - portfolio.getDislikes();
    }
}
