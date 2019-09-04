package ee.hm.dop.rest;

import ee.hm.dop.service.files.UploadedFileService;
import ee.hm.dop.service.files.enums.FileDirectory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@Controller
public class SitemapResource {

    @Inject
    private UploadedFileService uploadedFileService;

    @GetMapping(value = "/{filename}", produces = APPLICATION_XML_VALUE)
    @ResponseBody
    public FileSystemResource getSitemap(@PathVariable("filename") String filename) {
        return new FileSystemResource(uploadedFileService.getXmlFile(filename, FileDirectory.SITEMAPS));
    }
}