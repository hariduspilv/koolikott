package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.Thumbnail;
import ee.hm.dop.model.enums.Size;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import javax.inject.Inject;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ThumbnailDaoTest extends DatabaseTestBase {

    @Inject
    private ThumbnailDao thumbnailDao;

    @Test
    public void findByNameAndSize() {
        List<Thumbnail> all = thumbnailDao.findAll();
        if (CollectionUtils.isNotEmpty(all)) {
            Thumbnail anyThumbNail = all.get(0);
            Thumbnail byNameAndSize = thumbnailDao.findByNameAndSize(anyThumbNail.getName(), anyThumbNail.getSize());
            assertEquals(anyThumbNail.getId(), byNameAndSize.getId());
        }
    }
}
