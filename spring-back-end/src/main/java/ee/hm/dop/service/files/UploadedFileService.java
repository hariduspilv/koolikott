package ee.hm.dop.service.files;

import ee.hm.dop.config.Configuration;
import ee.hm.dop.dao.UploadedFileDao;
import ee.hm.dop.model.UploadedFile;
import ee.hm.dop.service.files.enums.FileDirectory;
import ee.hm.dop.utils.DOPFileUtils;
import ee.hm.dop.utils.DopConstants;
import ee.hm.dop.utils.io.LimitedSizeInputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static ee.hm.dop.utils.ConfigurationProperties.DOCUMENT_MAX_FILE_SIZE;
import static ee.hm.dop.utils.ConfigurationProperties.SERVER_ADDRESS;
import static java.lang.String.format;

@Slf4j
@Service
@Transactional
public class UploadedFileService {

    private static final String FILENAME_TOO_LONG_RESPONSE = "{\"cause\": \"filename too long\"}";

    @Inject
    private UploadedFileDao uploadedFileDao;
    @Inject
    private Configuration configuration;
    @Inject
    private ZipService zipService;
    @Inject
    Environment environment;

    private UploadedFile getUploadedFileById(Long id) {
        return uploadedFileDao.findById(id);
    }

    public ResponseEntity<InputStreamResource> getArchivedFile(Long fileId) {
        String sourcePath = getUploadedFileById(fileId).getPath();
        File zipFile = FileUtils.getFile(sourcePath + DopConstants.ZIP_EXTENSION);
        if (zipFile.exists()) {
            String mediaType = DOPFileUtils.probeForMediaType(zipFile.getName());
            return returnFileStream(mediaType, zipFile.getName(), zipFile);
        }
        String outputPath = zipService.packArchive(sourcePath, sourcePath);
        File file = FileUtils.getFile(outputPath);
        String mediaType = DOPFileUtils.probeForMediaType(file.getName());

        return returnFileStream(mediaType, file.getName(), file);
    }

    public ResponseEntity<InputStreamResource> getFile(Long fileId, String requestFilename, FileDirectory fileDirectory) throws UnsupportedEncodingException {
        UploadedFile uploadedFile = getUploadedFileById(fileId);
        if (uploadedFile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        String mediaType = DOPFileUtils.probeForMediaType(requestFilename);
        String fileName = DOPFileUtils.getRealFilename(requestFilename, uploadedFile);
        String path = configuration.getString(fileDirectory.getDirectory()) + uploadedFile.getId() + File.separator + fileName;

        File file = new File(path);
        if (file.isDirectory() || !file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return returnFileStream(mediaType, fileName, file);
    }

    public File getXmlFile(String xmlfilename, FileDirectory fileDirectory) {
        UploadedFile uploadedFile = uploadedFileDao.findByField("name", xmlfilename);

        if (uploadedFile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        String fileName = DOPFileUtils.getRealFilename(xmlfilename, uploadedFile);
        String path = configuration.getString(fileDirectory.getDirectory()) + fileName;

        File file = new File(path);
        if (file.isDirectory() || !file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return file;
    }

    public ResponseEntity<?> uploadXmlFile(String file, String xmlFileName, FileDirectory fileDirectory) throws UnsupportedEncodingException {
        String property = environment.getProperty("server.servlet.contextPath");
        String filenameCleaned = DOPFileUtils.cleanFileName(xmlFileName);
        String filename = DOPFileUtils.encode(filenameCleaned);
        if (filename.length() > DopConstants.MAX_NAME_LENGTH) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FILENAME_TOO_LONG_RESPONSE);
        }
        UploadedFile newUploadedFile;

        UploadedFile uploadedFile = uploadedFileDao.findByField("name", xmlFileName);
        if (uploadedFile != null) {
            newUploadedFile = uploadedFileDao.createOrUpdate(existingUploadedFile(uploadedFile.getId(), filename));
        } else {
            newUploadedFile = uploadedFileDao.createOrUpdate(newUploadedFile(filename));
        }
        String path = configuration.getString(fileDirectory.getDirectory()) + filename;
        String url = configuration.getString(SERVER_ADDRESS) + property + "/" + newUploadedFile.getName();
        newUploadedFile.setPath(path);
        newUploadedFile.setUrl(url);

        InputStream inputStream = new ByteArrayInputStream(file.getBytes());
        writeToFile(inputStream, path, xmlFileName);

        UploadedFile updated = uploadedFileDao.createOrUpdate(newUploadedFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(updated);
    }


    /**
     * After creating uploaded file object to DB, upload file to server
     */
    public ResponseEntity<?> uploadFile(MultipartFile file, FileDirectory fileDirectory, String restPath) throws UnsupportedEncodingException {
        String name = file.getOriginalFilename() != null ? file.getOriginalFilename() : file.getName();
        String filenameCleaned = DOPFileUtils.cleanFileName(name);
        String filename = DOPFileUtils.encode(filenameCleaned);
        if (filename.length() > DopConstants.MAX_NAME_LENGTH) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FILENAME_TOO_LONG_RESPONSE);
        }

        UploadedFile newUploadedFile = uploadedFileDao.createOrUpdate(newUploadedFile(filename));

        String path = configuration.getString(fileDirectory.getDirectory()) + newUploadedFile.getId() + "/" + filename;
        String url = configuration.getString(SERVER_ADDRESS) + restPath + newUploadedFile.getId() + "/" + newUploadedFile.getName();
        newUploadedFile.setPath(path);
        newUploadedFile.setUrl(url);

        try {
            writeToFile(file.getInputStream(), path, name);
        } catch (IOException e) {
            log.info("file write exception {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File write error");
        }

        UploadedFile updated = uploadedFileDao.createOrUpdate(newUploadedFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(updated);
    }

    private void writeToFile(InputStream fileInputStream, String path, String fileName) {
        LimitedSizeInputStream limitedSizeInputStream = new LimitedSizeInputStream(configuration.getInt(DOCUMENT_MAX_FILE_SIZE), fileInputStream);
        String extension = FilenameUtils.getExtension(fileName);
        if (Objects.equals(extension, DopConstants.EBOOK_EXTENSION)) {
            DOPFileUtils.unpackArchive(limitedSizeInputStream, path);
        } else {
            DOPFileUtils.writeToFile(limitedSizeInputStream, path);
        }
    }

    private UploadedFile newUploadedFile(String filename) {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setName(filename);
        return uploadedFile;
    }

    private UploadedFile existingUploadedFile(Long id, String filename) {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setName(filename);
        uploadedFile.setId(id);
        return uploadedFile;
    }

    public ResponseEntity<InputStreamResource> returnFileStream(String mediaType, String fileName, File file) {
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.add(DopConstants.CONTENT_DISPOSITION, "Attachment; filename*=\"UTF-8''" + DOPFileUtils.encode(fileName) + "\"; filename=\"" + fileName + "\"");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType(mediaType))
                    .body(resource);
        } catch (Exception e) {
            log.info(format("Downloading file failed: %s Media type: %s File name: %s", e.getMessage(), mediaType, fileName), e);
            return null;
        }
    }

    public ResponseEntity<InputStreamResource> returnFileStreamForPic(String mediaType, String fileName, long length, InputStream stream) {
        try {
            InputStreamResource resource = new InputStreamResource(stream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(DopConstants.CONTENT_DISPOSITION, "Attachment; filename*=\"UTF-8''" + DOPFileUtils.encode(fileName) + "\"; filename=\"" + fileName + "\"");
            return ResponseEntity.ok()
                    .headers(headers)
                    .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
                    .contentLength(length)
                    .contentType(MediaType.parseMediaType(mediaType))
                    .body(resource);
        } catch (Exception e) {
            log.info("Downloading file failed: {}", e.getMessage(), e);
            return null;
        }
    }

    public ResponseEntity<InputStreamResource> returnFileStreamForPdf(String mediaType, InputStream stream, String disposition) {
        try {
            InputStreamResource resource = new InputStreamResource(stream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(DopConstants.CONTENT_DISPOSITION, disposition);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(mediaType))
                    .body(resource);
        } catch (Exception e) {
            log.info("Downloading file failed: {}", e.getMessage(), e);
            return null;
        }
    }
}
