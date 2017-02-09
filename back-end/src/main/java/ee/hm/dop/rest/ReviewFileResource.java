package ee.hm.dop.rest;

import static ee.hm.dop.utils.ConfigurationProperties.FILE_REVIEW_DIRECTORY;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.UploadedFile;
import ee.hm.dop.service.UploadedFileService;
import org.apache.commons.configuration.Configuration;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * Created by joonas on 6.10.16.
 */

@Path("review")
public class ReviewFileResource extends BaseResource {

    @Inject
    private Configuration configuration;

    @Inject
    private UploadedFileService uploadedFileService;

    @POST
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public UploadedFile uploadReview(@FormDataParam("review") InputStream review,
                                     @FormDataParam("review") FormDataContentDisposition reviewDetail) throws UnsupportedEncodingException {
        return uploadedFileService.uploadFile(review, reviewDetail, configuration.getString(FILE_REVIEW_DIRECTORY), "/rest/review/");
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("{id}/{filename}")
    public Response getFile(@PathParam("id") Long fileId, @PathParam("filename") String filename) throws UnsupportedEncodingException {
        return uploadedFileService.getFile(fileId, filename, true);
    }
}
