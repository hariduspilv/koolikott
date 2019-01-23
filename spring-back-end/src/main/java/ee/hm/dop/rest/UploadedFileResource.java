package ee.hm.dop.rest;

import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.files.UploadedFileService;
import ee.hm.dop.service.files.enums.FileDirectory;
import org.apache.commons.configuration2.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;

import static ee.hm.dop.utils.ConfigurationProperties.DOCUMENT_MAX_FILE_SIZE;

@RestController
@RequestMapping("uploadedFile")
public class UploadedFileResource extends BaseResource {

    public static final String REST_UPLOADED_FILE = "/rest" + "/uploadedFile/"; //todo was constant
    @Inject
    private Configuration configuration;
    @Inject
    private UploadedFileService uploadedFileService;

    @PostMapping
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile review) throws UnsupportedEncodingException {
        return uploadedFileService.uploadFile(review, FileDirectory.UPDATE, REST_UPLOADED_FILE);
    }

    @GetMapping(value = "{id}/{filename:.*}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("id") Long fileId,
                                                       @PathVariable("filename") String filename,
                                                       @RequestParam(value = "archive", defaultValue = "false") boolean archive) throws UnsupportedEncodingException {
        if (archive) return uploadedFileService.getArchivedFile(fileId);
        return uploadedFileService.getFile(fileId, filename, FileDirectory.UPDATE);
    }

    @GetMapping("/maxSize")
    public int getMaxSize() {
        return configuration.getInt(DOCUMENT_MAX_FILE_SIZE);
    }

}
