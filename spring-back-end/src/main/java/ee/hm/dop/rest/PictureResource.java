package ee.hm.dop.rest;

import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Picture;
import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.model.enums.Size;
import ee.hm.dop.service.files.PictureSaver;
import ee.hm.dop.service.files.PictureService;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.HttpURLConnection;

import static ee.hm.dop.utils.ConfigurationProperties.MAX_FILE_SIZE;
import static ee.hm.dop.utils.DOPFileUtils.read;
import static org.apache.commons.codec.binary.Base64.decodeBase64;

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

    @GetMapping
    @RequestMapping("/{name}")
    @Produces("image/png")
    public Response getPictureDataByName(@PathVariable("name") String pictureName) {
        return getPictureResponseWithCache(pictureService.getByName(pictureName));
    }

    @GetMapping
    @RequestMapping("thumbnail/{size}/{name}")
    @Produces("image/png")
    public Response getSMThumbnailDataByName(@PathVariable("size") String sizeString, @PathVariable("name") String pictureName) {
        if (StringUtils.isBlank(sizeString)) {
            throw new UnsupportedOperationException("no size");
        }
        Size size = Size.valueOf(sizeString.toUpperCase());
        return getPictureResponseWithCache(pictureService.getThumbnailByName(pictureName, size));
    }

    private Response getPictureResponseWithCache(Picture picture) {
        if (picture != null) {
            byte[] data = picture.getData();
            return Response.ok(data).header(HttpHeaders.CACHE_CONTROL, MAX_AGE_1_YEAR).build();
        }
        return Response.status(HttpURLConnection.HTTP_NOT_FOUND).build();
    }

    @PostMapping
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Picture uploadPicture(@FormDataParam("picture") InputStream fileInputStream) {
        byte[] dataBase64 = read(fileInputStream, configuration.getInt(MAX_FILE_SIZE));
        byte[] data = decodeBase64(dataBase64);

        Picture picture = new OriginalPicture();
        picture.setData(data);
        return pictureSaver.create(picture);
    }

    @PutMapping
    @RequestMapping("/fromUrl")
    public Picture uploadPictureFromURL(@RequestBody String url) {
        return pictureSaver.createFromURL(url);
    }

    @GetMapping
    @RequestMapping(value = "/maxSize", produces = org.springframework.http.MediaType.TEXT_PLAIN_VALUE)
    public int getMaxSize() {
        return configuration.getInt(MAX_FILE_SIZE);
    }
}
