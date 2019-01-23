package ee.hm.dop.rest;

import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.model.enums.Size;
import ee.hm.dop.service.files.PictureSaver;
import ee.hm.dop.service.files.PictureService;
import ee.hm.dop.service.files.UploadedFileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;

import static ee.hm.dop.utils.ConfigurationProperties.MAX_FILE_SIZE;
import static ee.hm.dop.utils.DOPFileUtils.read;
import static org.apache.commons.codec.binary.Base64.decodeBase64;

@Slf4j
@RestController
@RequestMapping("picture")
public class PictureResource extends BaseResource {

    public static final String MAX_AGE_1_YEAR = "max-age=31536000";
    @Inject
    private PictureService pictureService;
    @Inject
    private Configuration configuration;
    @Inject
    private PictureSaver pictureSaver;
    @Inject
    private UploadedFileService uploadedFileService;

    @GetMapping(value = "/{name}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<InputStreamResource> getPictureDataByName(@PathVariable("name") String pictureName) {
        return getPictureResponseWithCache(pictureService.getByName(pictureName), pictureName);
    }

    @GetMapping(value = "thumbnail/{size}/{name}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<InputStreamResource> getSMThumbnailDataByName(@PathVariable("size") String sizeString, @PathVariable("name") String pictureName) {
        if (StringUtils.isBlank(sizeString)) {
            throw new UnsupportedOperationException("no size");
        }
        Size size = Size.valueOf(sizeString.toUpperCase());
        return getPictureResponseWithCache(pictureService.getThumbnailByName(pictureName, size), pictureName);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public Picture uploadPicture(@RequestParam("picture") MultipartFile fileInputStream) {
        try {
            byte[] dataBase64 = read(fileInputStream.getInputStream(), configuration.getInt(MAX_FILE_SIZE));
            byte[] data = decodeBase64(dataBase64);

            Picture picture = new OriginalPicture();
            picture.setData(data);
            return pictureSaver.create(picture);
        } catch (Exception e) {
            log.info("error uploading file: {}", e.getMessage(), e);
            return null;
        }
    }

    @PutMapping("/fromUrl")
    public Picture uploadPictureFromURL(@RequestBody String url) {
        return pictureSaver.createFromURL(url);
    }

    @GetMapping(value = "/maxSize")
    public int getMaxSize() {
        return configuration.getInt(MAX_FILE_SIZE);
    }

    private ResponseEntity<InputStreamResource> getPictureResponseWithCache(Picture picture, String pictureName) {
        if (picture == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        byte[] data = picture.getData();
        return uploadedFileService.returnFileStreamForPic(MediaType.IMAGE_PNG_VALUE, pictureName, data.length, new ByteArrayInputStream(data));
    }
}
