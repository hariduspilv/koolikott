package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.inject.Inject;
import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.UploadedFile;
import org.junit.Test;

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/UploadedFileDaoTest.java
/**
 * Created by mart on 8.08.16.
 */
=======
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/UploadedFileDaoTest.java
public class UploadedFileDaoTest extends DatabaseTestBase {

    @Inject
    private UploadedFileDao uploadedFileDao;

    @Test
    public void findUploadedFileById() {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/dao/UploadedFileDaoTest.java
        long fileId = 1;
        UploadedFile uploadedFile = uploadedFileDao.findById(fileId);
=======
        UploadedFile uploadedFile = uploadedFileDao.findById(1L);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/dao/UploadedFileDaoTest.java
        assertNotNull(uploadedFile);
        assertEquals("bookCover.jpg", uploadedFile.getName());
    }
}
