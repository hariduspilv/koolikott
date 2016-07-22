package ee.hm.dop.service;

import static ee.hm.dop.utils.FileUtils.getFileAsStream;
import static ee.hm.dop.utils.FileUtils.read;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import ee.hm.dop.dao.PictureDAO;
import ee.hm.dop.model.Picture;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class PictureServiceTest {

    private static final String IMAGE1_SHA1_HASH = "ce7870d17769da42406687d2ad72713ea3b4a6bd";

    @TestSubject
    private PictureService pictureService = new PictureService();

    @Mock
    private PictureDAO pictureDAO;

    @Test
    public void create() {
        byte[] image1 = read(getFileAsStream("images/image1.jpg"), 1);

        Picture picture = new Picture();
        picture.setData(image1);

        expect(pictureDAO.findByName(IMAGE1_SHA1_HASH)).andReturn(null);
        expect(pictureDAO.update(picture)).andReturn(picture);

        replay(pictureDAO);

        Picture createdPicture = pictureService.create(picture);

        verify(pictureDAO);

        assertNotNull(createdPicture.getName());
        assertArrayEquals(image1, createdPicture.getData());
    }

    @Test
    public void createWhenHashMatches() {
        Picture existingPicture = new Picture();
        existingPicture.setId(1);

        byte[] image1 = read(getFileAsStream("images/image1.jpg"), 1);

        Picture picture = new Picture();
        picture.setData(image1);

        expect(pictureDAO.findByName(IMAGE1_SHA1_HASH)).andReturn(existingPicture);

        replay(pictureDAO);

        Picture createdPicture = pictureService.create(picture);

        verify(pictureDAO);

        assertEquals(existingPicture, createdPicture);
        assertEquals(existingPicture.getId(), createdPicture.getId());
    }
}
