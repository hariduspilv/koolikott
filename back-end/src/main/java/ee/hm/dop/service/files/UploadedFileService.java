package ee.hm.dop.service.files;

import ee.hm.dop.dao.UploadedFileDao;
import ee.hm.dop.model.UploadedFile;
import ee.hm.dop.utils.DopConstants;
import ee.hm.dop.service.files.enums.FileDirectory;
import ee.hm.dop.utils.DOPFileUtils;
import ee.hm.dop.utils.io.LimitedSizeInputStream;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import static ee.hm.dop.utils.ConfigurationProperties.DOCUMENT_MAX_FILE_SIZE;
import static ee.hm.dop.utils.ConfigurationProperties.SERVER_ADDRESS;

public class UploadedFileService {

    private static final String FILENAME_TOO_LONG_RESPONSE = "{\"cause\": \"filename too long\"}";

    @Inject
    private UploadedFileDao uploadedFileDao;
    @Inject
    private Configuration configuration;
    @Inject
    private ZipService zipService;

    private UploadedFile getUploadedFileById(Long id) {
        return uploadedFileDao.findById(id);
    }

    public Response getArchivedFile(Long fileId) {
        String sourcePath = getUploadedFileById(fileId).getPath();
        File zipFile = FileUtils.getFile(sourcePath + DopConstants.ZIP_EXTENSION);
        if (zipFile.exists()) {
            return Response.ok(zipFile).build();
        }
        String outputPath = zipService.packArchive(sourcePath, sourcePath);
        return Response.ok(FileUtils.getFile(outputPath)).build();
    }

    public Response getFile(Long fileId, String requestFilename, FileDirectory fileDirectory) throws UnsupportedEncodingException {
        UploadedFile file = getUploadedFileById(fileId);
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        String mediaType = DOPFileUtils.probeForMediaType(requestFilename);
        String fileName = DOPFileUtils.getRealFilename(requestFilename, file);
        String path = configuration.getString(fileDirectory.getDirectory()) + file.getId() + File.separator + fileName;

        File newFile = new File(path);
        if (newFile.isDirectory()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        if (!newFile.exists()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        /* Safari can serve directly decoded filenames, chrome/IE can server utf-8 encoded filenames */
        return Response.ok(FileUtils.getFile(path), mediaType)
                .header(DopConstants.CONTENT_DISPOSITION, "Inline; filename*=\"UTF-8''" + DOPFileUtils.encode(fileName) + "\"; filename=\"" + fileName + "\"")
                .build();
    }

    /**
     * After creating uploaded file object to DB, upload file to server
     */
    public Response uploadFile(InputStream fileInputStream, FormDataContentDisposition fileDetail, FileDirectory fileDirectory, String restPath) throws UnsupportedEncodingException {
        String filenameCleaned = DOPFileUtils.cleanFileName(fileDetail.getFileName());
        String filename = DOPFileUtils.encode(filenameCleaned);
        if (filename.length() > DopConstants.MAX_NAME_LENGTH) {
            return Response.status(Response.Status.BAD_REQUEST).entity(FILENAME_TOO_LONG_RESPONSE).build();
        }

        UploadedFile newUploadedFile = uploadedFileDao.createOrUpdate(newUploadedFile(filename));

        String path = configuration.getString(fileDirectory.getDirectory()) + newUploadedFile.getId() + "/" + filename;
        String url = configuration.getString(SERVER_ADDRESS) + restPath + newUploadedFile.getId() + "/" + newUploadedFile.getName();
        newUploadedFile.setPath(path);
        newUploadedFile.setUrl(url);

        writeToFile(fileInputStream, fileDetail, path);

        UploadedFile updated = uploadedFileDao.createOrUpdate(newUploadedFile);
        return Response.status(Response.Status.CREATED).entity(updated).build();
    }

    private void writeToFile(InputStream fileInputStream, FormDataContentDisposition fileDetail, String path) {
        LimitedSizeInputStream limitedSizeInputStream = new LimitedSizeInputStream(configuration.getInt(DOCUMENT_MAX_FILE_SIZE), fileInputStream);
        String extension = FilenameUtils.getExtension(fileDetail.getFileName());
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
}
