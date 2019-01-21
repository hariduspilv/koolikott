package ee.hm.dop.rest;

import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.files.UploadedFileService;
import ee.hm.dop.service.files.enums.FileDirectory;
import org.apache.commons.configuration2.Configuration;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static ee.hm.dop.utils.ConfigurationProperties.DOCUMENT_MAX_FILE_SIZE;

@RestController
@RequestMapping("uploadedFile")
public class UploadedFileResource extends BaseResource {

    public static final String REST_UPLOADED_FILE = "rest" + "/uploadedFile/"; //todo was constant
    @Inject
    private Configuration configuration;
    @Inject
    private UploadedFileService uploadedFileService;

    @PostMapping
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    @Consumes(MediaType.MULTIPART_FORM_DATA)

    public Response uploadFile(@FormDataParam("file") InputStream fileInputStream,
                               @FormDataParam("file") FormDataContentDisposition fileDetail) throws UnsupportedEncodingException {
        return uploadedFileService.uploadFile(fileInputStream, fileDetail, FileDirectory.UPDATE, REST_UPLOADED_FILE);
    }

    @GetMapping
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @RequestMapping("{id}/{filename:.*}")
    public Response getFile(@PathVariable("id") Long fileId, @PathVariable("filename") String filename, @RequestParam("archive") boolean archive) throws UnsupportedEncodingException {
        if (archive) return uploadedFileService.getArchivedFile(fileId);
        return uploadedFileService.getFile(fileId, filename, FileDirectory.UPDATE);
    }

    @GetMapping
    @RequestMapping("/maxSize")

    public int getMaxSize() {
        return configuration.getInt(DOCUMENT_MAX_FILE_SIZE);
    }

}
