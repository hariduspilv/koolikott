package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.inject.Inject;
import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.UploadedFile;
import org.junit.Test;

/**
 * Created by mart on 8.08.16.
 */
public class UploadedFileDaoTest extends DatabaseTestBase {

    @Inject
    private UploadedFileDao uploadedFileDao;

    @Test
    public void findUploadedFileById() {
        long fileId = 1;
        UploadedFile uploadedFile = uploadedFileDao.findById(fileId);
        assertNotNull(uploadedFile);
        assertEquals("bookCover.jpg", uploadedFile.getName());
    }
}
