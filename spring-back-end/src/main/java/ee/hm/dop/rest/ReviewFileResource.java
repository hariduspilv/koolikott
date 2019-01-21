package ee.hm.dop.rest;

import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.files.UploadedFileService;
import ee.hm.dop.service.files.enums.FileDirectory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("review")
public class ReviewFileResource extends BaseResource {

    public static final String REST_REVIEW = "rest" + "/review/"; //todo was constant
    @Inject
    private UploadedFileService uploadedFileService;

    @PostMapping
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadReview(@FormDataParam("review") InputStream review,
                                 @FormDataParam("review") FormDataContentDisposition reviewDetail) throws UnsupportedEncodingException {
        return uploadedFileService.uploadFile(review, reviewDetail, FileDirectory.REVIEW, REST_REVIEW);
    }

    @GetMapping
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @RequestMapping("{id}/{filename}")
    public Response getFile(@PathVariable("id") Long fileId, @PathVariable("filename") String filename) throws UnsupportedEncodingException {
        return uploadedFileService.getFile(fileId, filename, FileDirectory.REVIEW);
    }
}
