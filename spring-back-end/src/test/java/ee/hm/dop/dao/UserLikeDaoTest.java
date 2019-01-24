package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Searchable;
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserLikeDaoTest extends DatabaseTestBase {

    @Inject
    private UserLikeDao userLikeDao;

    //todo failing?
    @Ignore
    @Test
    public void findMostLikedSince() {
        List<Searchable> objects = userLikeDao.findMostLikedSince(now().minusDays(100), 5);
        assertEquals(3, objects.size());

        assertEquals(Long.valueOf(103), objects.get(0).getId());
        assertEquals(Long.valueOf(1), objects.get(1).getId());
        assertEquals(Long.valueOf(2), objects.get(2).getId());
    }

    @Test
    public void findMostLikedSinceFuture() {
        List<Searchable> objects = userLikeDao.findMostLikedSince(now().plusDays(1), 5);
        assertTrue(objects.isEmpty());
    }
}
