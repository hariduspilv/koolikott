package ee.hm.dop.service;

import static ee.hm.dop.service.ZipService.ZIP_EXTENSION;
import static ee.hm.dop.utils.ConfigurationProperties.DOCUMENT_MAX_FILE_SIZE;
import static ee.hm.dop.utils.ConfigurationProperties.FILE_REVIEW_DIRECTORY;
import static ee.hm.dop.utils.ConfigurationProperties.FILE_UPLOAD_DIRECTORY;
import static ee.hm.dop.utils.ConfigurationProperties.SERVER_ADDRESS;
import static ee.hm.dop.utils.DOPFileUtils.writeToFile;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.dao.UploadedFileDao;
import ee.hm.dop.utils.io.LimitedSizeInputStream;
import ee.hm.dop.model.UploadedFile;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

public class UploadedFileService {

    private static final String EBOOK_EXTENSION = "epub";

    private final String FILENAME_TOO_LONG_RESPONSE = "{\"cause\": \"filename too long\"}";

    @Inject
    private UploadedFileDao uploadedFileDao;
    @Inject
    private Configuration configuration;
    @Inject
    private ZipService zipService;

    private UploadedFile getUploadedFileById(Long id) {
        return uploadedFileDao.findById(id);
    }

    private UploadedFile update(UploadedFile uploadedFile) {
        return uploadedFileDao.createOrUpdate(uploadedFile);
    }

    private UploadedFile create(UploadedFile uploadedFile) {
        return uploadedFileDao.createOrUpdate(uploadedFile);
    }

    public Response getArchivedFile(Long fileId) {
        String sourcePath = getUploadedFileById(fileId).getPath();

        //  Check if the zip archive already exists
        File zipFile = FileUtils.getFile(sourcePath + ZIP_EXTENSION);
        if (zipFile.exists()) return Response.ok(FileUtils.getFile(sourcePath + ZIP_EXTENSION)).build();

        String outputPath = zipService.packArchive(sourcePath, sourcePath);

        return Response.ok(FileUtils.getFile(outputPath)).build();
    }

    public Response getFile(Long fileId, String filename, final boolean isReview) throws UnsupportedEncodingException {
        UploadedFile file = getUploadedFileById(fileId);
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String mediaType;
        try {
            mediaType = Files.probeContentType(Paths.get(filename.toLowerCase()));
        } catch (IOException e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        String fileName = file.getName();
        if (filename.contains("/")) {
            fileName = file.getName() + filename.substring(filename.indexOf("/"), filename.length());
        }

        final String directoryConstant = isReview ? FILE_REVIEW_DIRECTORY : FILE_UPLOAD_DIRECTORY;
        String path = configuration.getString(directoryConstant) + file.getId() + File.separator + fileName;

        if (new File(path).isDirectory()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        if (!new File(path).exists()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        /* Safari can serve directly decoded filenames, chrome/IE can server utf-8 encoded filenames */
        return Response.ok(FileUtils.getFile(path), mediaType)
                .header("Content-Disposition", "Inline; filename*=\"UTF-8''" + URLEncoder.encode(fileName, UTF_8.name()) + "\"; filename=\"" + fileName + "\"")
                .build();
    }

    public Response uploadFile(InputStream fileInputStream, FormDataContentDisposition fileDetail, String rootPath, String restPath) throws UnsupportedEncodingException {
        String extension = FilenameUtils.getExtension(fileDetail.getFileName());
        LimitedSizeInputStream limitedSizeInputStream = new LimitedSizeInputStream(configuration.getInt(DOCUMENT_MAX_FILE_SIZE), fileInputStream);

        UploadedFile uploadedFile = new UploadedFile();
        String filenameCleaned = fileDetail.getFileName().replaceAll(" ", "+");
        String filename = URLEncoder.encode(filenameCleaned, UTF_8.name());

        if (filename.length() > 255) {
            // Add informative response so ajax could identify the cause of bad response
            return Response.status(Response.Status.BAD_REQUEST).entity(FILENAME_TOO_LONG_RESPONSE).build();
        }

        uploadedFile.setName(filename);
        uploadedFile = create(uploadedFile);

        //After creating uploaded file object to DB, upload file to server
        String path = rootPath + uploadedFile.getId() + "/" + filename;
        String url = configuration.getString(SERVER_ADDRESS) + restPath + uploadedFile.getId() + "/" + uploadedFile.getName();
        uploadedFile.setPath(path);
        uploadedFile.setUrl(url);

        if (Objects.equals(extension, EBOOK_EXTENSION)) {
            zipService.unpackArchive(limitedSizeInputStream, path);
        } else {
            writeToFile(limitedSizeInputStream, path);
        }

        return Response.status(Response.Status.CREATED).entity(update(uploadedFile)).build();
    }
}
