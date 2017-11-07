package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Tag;
import org.junit.Test;

public class TagDaoTest extends DatabaseTestBase {

    @Inject
    private TagDao tagDao;

    @Test
    public void findTagByName() {
        Long id = 1L;
        String name = "matemaatika";

        Tag returnedTag = tagDao.findByName(name);

        assertNotNull(returnedTag);
        assertNotNull(returnedTag.getId());
        assertEquals(id, returnedTag.getId());
        assertEquals(name, returnedTag.getName());
    }
}
