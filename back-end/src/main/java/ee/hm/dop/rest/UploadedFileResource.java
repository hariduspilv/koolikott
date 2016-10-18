package ee.hm.dop.rest;

import static ee.hm.dop.utils.ConfigurationProperties.DOCUMENT_MAX_FILE_SIZE;
import static ee.hm.dop.utils.ConfigurationProperties.FILE_UPLOAD_DIRECTORY;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

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

@Path("uploadedFile")
public class UploadedFileResource extends BaseResource {

    @Inject
    private Configuration configuration;

    @Inject
    private UploadedFileService uploadedFileService;

    @POST
    @RolesAllowed({"USER", "ADMIN", "MODERATOR"})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public UploadedFile uploadFile(@FormDataParam("file") InputStream fileInputStream,
                                   @FormDataParam("file") FormDataContentDisposition fileDetail) throws UnsupportedEncodingException {
        return uploadedFileService.uploadFile(fileInputStream, fileDetail, configuration.getString(FILE_UPLOAD_DIRECTORY), "/rest/uploadedFile/");
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("{id}/{filename}")
    public Response getFile(@PathParam("id") Long fileId, @PathParam("filename") String filename) {
        return uploadedFileService.getFile(fileId, filename);
    }

    @GET
    @Path("/maxSize")
    @Produces(MediaType.APPLICATION_JSON)
    public int getMaxSize() {
        return configuration.getInt(DOCUMENT_MAX_FILE_SIZE);
    }

}
