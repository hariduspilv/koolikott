package ee.hm.dop.service;

import static ee.hm.dop.utils.DOPFileUtils.getFileAsStream;
import static ee.hm.dop.utils.DOPFileUtils.read;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import ee.hm.dop.dao.PictureDAO;
import ee.hm.dop.dao.ThumbnailDAO;
import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Thumbnail;
import org.easymock.*;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class PictureServiceTest {

    private static final String IMAGE1_SHA1_HASH = "ce7870d17769da42406687d2ad72713ea3b4a6bd";

    @TestSubject
    private PictureService pictureService = new PictureService();

    @Mock
    private PictureDAO pictureDAO;

    @Mock
    private ThumbnailDAO thumbnailDAO;

    @Test
    public void create() {
        byte[] image1 = read(getFileAsStream("images/image1.jpg"), 1);

        Picture picture = new OriginalPicture();
        picture.setData(image1);

        Thumbnail thumbnail = new Thumbnail();
        thumbnail.setData(image1);

        expect(pictureDAO.findByName(IMAGE1_SHA1_HASH)).andReturn(null);
        expect(thumbnailDAO.update(EasyMock.anyObject(Thumbnail.class))).andReturn(new Thumbnail()).times(4);
        expect(pictureDAO.update((OriginalPicture) picture)).andReturn((OriginalPicture) picture);

        replay(thumbnailDAO);
        replay(pictureDAO);

        Picture createdPicture = pictureService.create(picture);

        verify(pictureDAO);

        assertNotNull(createdPicture.getName());
        assertArrayEquals(picture.getData(), createdPicture.getData());
    }


    @Test
    public void createWhenHashMatches() {
        Picture existingPicture = new OriginalPicture();
        existingPicture.setId(1);

        byte[] image1 = read(getFileAsStream("images/image1.jpg"), 1);
        existingPicture.setData(image1);

        Picture picture = new OriginalPicture();
        picture.setData(image1);

        expect(pictureDAO.findByName(IMAGE1_SHA1_HASH)).andReturn(existingPicture).anyTimes();

        replay(pictureDAO);

        Picture createdPicture = pictureService.create(picture);

        verify(pictureDAO);

        assertEquals(existingPicture, createdPicture);
        assertEquals(existingPicture.getId(), createdPicture.getId());
    }
}
