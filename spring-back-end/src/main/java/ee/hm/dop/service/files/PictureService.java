package ee.hm.dop.service.files;

import ee.hm.dop.dao.OriginalPictureDao;
import ee.hm.dop.dao.ThumbnailDao;
import ee.hm.dop.model.LicenseType;
import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.Thumbnail;
import ee.hm.dop.model.enums.Size;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static ee.hm.dop.model.enums.LicenseType.CC_BY_SA_30;

@Service
@Transactional
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

    public LicenseType getLicenceTypeById(Long id) {
        return originalPictureDao.findById(id).getLicenseType();
    }

    public void setLicenceType(Long id, LicenseType licenseType) {
        OriginalPicture originalPicture = originalPictureDao.findById(id);
        if (originalPicture != null) {
            originalPicture.setLicenseType(licenseType);
        }
    }

    public boolean pictureHasUnAcceptableLicence(Picture picture) {
        LicenseType pictureLicenceType = getLicenceTypeById(picture.getId());
        if (pictureLicenceType == null) {
            return true;
        }
        return !pictureLicenceType.getName().equals(CC_BY_SA_30);
    }
}
