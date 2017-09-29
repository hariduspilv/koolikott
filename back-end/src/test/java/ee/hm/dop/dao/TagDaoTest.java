package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Tag;
import org.junit.Test;

/**
 * Created by mart.laus on 24.07.2015.
 */
public class TagDaoTest extends DatabaseTestBase {

    @Inject
    private TagDao tagDao;

    @Test
    public void findTagByName() {
        Long id = new Long(1);
        String name = "matemaatika";

        Tag returnedTag = tagDao.findByName(name);

        assertNotNull(returnedTag);
        assertNotNull(returnedTag.getId());
        assertEquals(id, returnedTag.getId());
        assertEquals(name, returnedTag.getName());
    }
}
