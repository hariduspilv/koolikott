package ee.hm.dop.service.files;

import ee.hm.dop.dao.OriginalPictureDao;
import ee.hm.dop.dao.ThumbnailDao;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Thumbnail;
import ee.hm.dop.model.enums.Size;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class PictureService {

    @Inject
    private OriginalPictureDao originalPictureDao;
    @Inject
    private ThumbnailDao thumbnailDao;
    @Inject
    private PictureSaver pictureSaver;

    public Picture getByName(String name) {
        return originalPictureDao.findByNameAny(name);
    }

    public Thumbnail getThumbnailByName(String name, Size size) {
        Thumbnail thumbnail = thumbnailDao.findByNameAndSize(name, size);
        if (thumbnail != null) {
            return thumbnail;
        }
        Picture existingPicture = getByName(name);
        return existingPicture != null ? pictureSaver.createOneThumbnail(existingPicture, size) : null;
    }
}
