package ee.hm.dop.rest;

import ee.hm.dop.model.enums.RoleString;
import ee.hm.dop.service.files.UploadedFileService;
import ee.hm.dop.service.files.enums.FileDirectory;
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

@RestController
@RequestMapping("review")
public class ReviewFileResource extends BaseResource {

    public static final String REST_REVIEW = "/rest" + "/review/"; //todo was constant
    @Inject
    private UploadedFileService uploadedFileService;

    @PostMapping
    @Secured({RoleString.USER, RoleString.ADMIN, RoleString.MODERATOR})
    public ResponseEntity<?> uploadReview(@RequestParam("review") MultipartFile review) throws UnsupportedEncodingException {
        return uploadedFileService.uploadFile(review, FileDirectory.REVIEW, REST_REVIEW);
    }

    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @RequestMapping("{id}/{filename}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("id") Long fileId, @PathVariable("filename") String filename) throws UnsupportedEncodingException {
        return uploadedFileService.getFile(fileId, filename, FileDirectory.REVIEW);
    }
}
