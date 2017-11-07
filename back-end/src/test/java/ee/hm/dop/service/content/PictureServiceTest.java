package ee.hm.dop.service.content;

<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/service/content/PictureServiceTest.java
import static ee.hm.dop.utils.DOPFileUtils.getFileAsStream;
import static ee.hm.dop.utils.DOPFileUtils.read;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

=======
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/service/content/PictureServiceTest.java
import ee.hm.dop.dao.OriginalPictureDao;
import ee.hm.dop.dao.ThumbnailDao;
import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Thumbnail;
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/service/content/PictureServiceTest.java
import ee.hm.dop.service.content.PictureService;
import org.easymock.*;
=======
import ee.hm.dop.service.files.PictureSaver;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/service/content/PictureServiceTest.java
import org.junit.Test;
import org.junit.runner.RunWith;

import static ee.hm.dop.utils.DOPFileUtils.getFileAsStream;
import static ee.hm.dop.utils.DOPFileUtils.read;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

@RunWith(EasyMockRunner.class)
public class PictureServiceTest {

    private static final String IMAGE1_SHA1_HASH = "ce7870d17769da42406687d2ad72713ea3b4a6bd";

    @TestSubject
    private PictureSaver pictureSaver = new PictureSaver();
    @Mock
    private OriginalPictureDao originalPictureDao;
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/service/content/PictureServiceTest.java

=======
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/service/content/PictureServiceTest.java
    @Mock
    private ThumbnailDao thumbnailDao;

    @Test
    public void create() {
        byte[] image1 = read(getFileAsStream("images/image1.jpg"), 1);

        Picture picture = new OriginalPicture();
        picture.setData(image1);

        Thumbnail thumbnail = new Thumbnail();
        thumbnail.setData(image1);

        expect(originalPictureDao.findByName(IMAGE1_SHA1_HASH)).andReturn(null);
        expect(thumbnailDao.createOrUpdate(EasyMock.anyObject(Thumbnail.class))).andReturn(new Thumbnail()).times(4);
        expect(originalPictureDao.createOrUpdate((OriginalPicture) picture)).andReturn((OriginalPicture) picture);

        replay(thumbnailDao);
        replay(originalPictureDao);

        Picture createdPicture = pictureSaver.create(picture);

        verify(originalPictureDao);

        assertNotNull(createdPicture.getName());
        assertArrayEquals(picture.getData(), createdPicture.getData());
    }


    @Test
    public void createWhenHashMatches() {
        OriginalPicture existingPicture = new OriginalPicture();
        existingPicture.setId(1);

        byte[] image1 = read(getFileAsStream("images/image1.jpg"), 1);
        existingPicture.setData(image1);

        Picture picture = new OriginalPicture();
        picture.setData(image1);

        expect(originalPictureDao.findByName(IMAGE1_SHA1_HASH)).andReturn(existingPicture).anyTimes();

        replay(originalPictureDao);

        Picture createdPicture = pictureSaver.create(picture);

        verify(originalPictureDao);

        assertEquals(existingPicture, createdPicture);
        assertEquals(existingPicture.getId(), createdPicture.getId());
    }
}
