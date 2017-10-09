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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.files.UploadedFileService;
import ee.hm.dop.service.files.enums.FileDirectory;
import org.apache.commons.configuration.Configuration;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("uploadedFile")
public class UploadedFileResource extends BaseResource {

    public static final String REST_UPLOADED_FILE = "/rest/uploadedFile/";
    @Inject
    private Configuration configuration;
    @Inject
    private UploadedFileService uploadedFileService;

    @POST
    @RolesAllowed({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(@FormDataParam("file") InputStream fileInputStream,
                                   @FormDataParam("file") FormDataContentDisposition fileDetail) throws UnsupportedEncodingException {
        return uploadedFileService.uploadFile(fileInputStream, fileDetail, FileDirectory.UPDATE, REST_UPLOADED_FILE);
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("{id}/{filename:.*}")
    public Response getFile(@PathParam("id") Long fileId, @PathParam("filename") String filename, @QueryParam("archive") boolean archive) throws UnsupportedEncodingException {
        if (archive) return uploadedFileService.getArchivedFile(fileId);
        return uploadedFileService.getFile(fileId, filename, FileDirectory.UPDATE);
    }

    @GET
    @Path("/maxSize")
    @Produces(MediaType.APPLICATION_JSON)
    public int getMaxSize() {
        return configuration.getInt(DOCUMENT_MAX_FILE_SIZE);
    }

}
