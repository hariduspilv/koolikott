package ee.hm.dop.service.files;

import ee.hm.dop.dao.OriginalPictureDao;
import ee.hm.dop.dao.ThumbnailDao;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Thumbnail;
import ee.hm.dop.model.enums.Size;

import javax.inject.Inject;
import java.util.function.Function;

public class PictureService {

    @Inject
    private OriginalPictureDao originalPictureDao;
    @Inject
    private ThumbnailDao thumbnailDao;
    @Inject
    private PictureSaver pictureSaver;

    public Picture getByName(String name) {
        return originalPictureDao.findByName(name);
    }

    public Thumbnail getSMThumbnailByName(String name) {
        return getThumbNail(name, Size.SM, pictureSaver::tryToCreateSM);
    }

    public Thumbnail getSMLargeThumbnailByName(String name) {
        return getThumbNail(name, Size.SM_XS_XL, pictureSaver::tryToCreateSMLarge);
    }

    public Thumbnail getLGThumbnailByName(String name) {
        return getThumbNail(name, Size.LG, pictureSaver::tryToCreateLG);
    }

    public Thumbnail getLGLargeThumbnailByName(String name) {
        return getThumbNail(name, Size.LG_XS, pictureSaver::tryToCreateLGLarge);
    }

    private Thumbnail getThumbNail(String name, Size size, Function<Picture, Thumbnail> tryToCreate) {
        Thumbnail thumbnail = thumbnailDao.findByNameAndSize(name, size);
        if (thumbnail != null) {
            return thumbnail;
        }
        Picture existingPicture = getByName(name);
        return existingPicture != null ? tryToCreate.apply(existingPicture) : null;
    }
}
