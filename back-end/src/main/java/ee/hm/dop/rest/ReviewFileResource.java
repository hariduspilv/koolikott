package ee.hm.dop.rest;

import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.files.UploadedFileService;
import ee.hm.dop.service.files.enums.FileDirectory;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by joonas on 6.10.16.
 */

@Path("review")
public class ReviewFileResource extends BaseResource {

    public static final String REST_REVIEW = "/rest/review/";
    @Inject
    private UploadedFileService uploadedFileService;

    @POST
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadReview(@FormDataParam("review") InputStream review,
                                     @FormDataParam("review") FormDataContentDisposition reviewDetail) throws UnsupportedEncodingException {
        return uploadedFileService.uploadFile(review, reviewDetail, FileDirectory.REVIEW, REST_REVIEW);
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("{id}/{filename}")
    public Response getFile(@PathParam("id") Long fileId, @PathParam("filename") String filename) throws UnsupportedEncodingException {
        return uploadedFileService.getFile(fileId, filename, FileDirectory.REVIEW);
    }
}
