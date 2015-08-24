package ee.hm.dop.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import javax.inject.Inject;

import junit.framework.TestCase;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Tag;

/**
 * Created by mart.laus on 24.07.2015.
 */
public class TagDAOTest extends DatabaseTestBase {

    @Inject
    private TagDAO tagDAO;

    @Test
    public void findTagByName() {
        Long id = new Long(1);
        String name = "matemaatika";

        Tag returnedTag = tagDAO.findTagByName(name);

        assertNotNull(returnedTag);
        assertNotNull(returnedTag.getId());
        assertEquals(id, returnedTag.getId());
        TestCase.assertEquals(name, returnedTag.getName());
    }
}
