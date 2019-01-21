package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.UploadedFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UploadedFileDaoTest extends DatabaseTestBase {

    @Inject
    private UploadedFileDao uploadedFileDao;

    @Test
    public void findUploadedFileById() {
        UploadedFile uploadedFile = uploadedFileDao.findById(1L);
        assertNotNull(uploadedFile);
        assertEquals("bookCover.jpg", uploadedFile.getName());
    }
}
