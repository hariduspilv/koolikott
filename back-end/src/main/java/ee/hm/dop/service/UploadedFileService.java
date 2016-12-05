package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.*;
import static ee.hm.dop.utils.DOPFileUtils.unpackArchive;
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

import ee.hm.dop.dao.UploadedFileDAO;
import ee.hm.dop.io.LimitedSizeInputStream;
import ee.hm.dop.model.UploadedFile;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadedFileService {

    private static final String EBOOK_EXTENSION = "epub";
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private UploadedFileDAO uploadedFileDAO;

    @Inject
    private Configuration configuration;

    private UploadedFile getUploadedFileById(Long id) {
        return uploadedFileDAO.findUploadedFileById(id);
    }

    private UploadedFile update(UploadedFile uploadedFile) {
        return uploadedFileDAO.update(uploadedFile);
    }

    private UploadedFile create(UploadedFile uploadedFile) {
        return uploadedFileDAO.update(uploadedFile);
    }

    public Response getFile(Long fileId, String filename, final boolean isReview) {
        UploadedFile file = getUploadedFileById(fileId);
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String mediaType;
        try {
            mediaType = Files.probeContentType(Paths.get(filename));
        } catch (IOException e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        String fileName = file.getName();
        if(filename.contains("/")){
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

        return Response.ok(FileUtils.getFile(path), mediaType)
                .header("Content-Disposition", "Inline; filename=\"" + filename + "\"")
                .build();
    }

    public UploadedFile uploadFile(InputStream fileInputStream, FormDataContentDisposition fileDetail, String rootPath, String restPath) throws UnsupportedEncodingException {
        String extension = FilenameUtils.getExtension(fileDetail.getFileName());
        LimitedSizeInputStream limitedSizeInputStream = new LimitedSizeInputStream(configuration.getInt(DOCUMENT_MAX_FILE_SIZE), fileInputStream);

        UploadedFile uploadedFile = new UploadedFile();
        String filenameCleaned = fileDetail.getFileName().replaceAll(" ", "+");
        String filename = URLEncoder.encode(filenameCleaned, UTF_8.name());
        uploadedFile.setName(filename);
        uploadedFile = create(uploadedFile);

        //After creating uploaded file object to DB, upload file to server
        String path = rootPath + uploadedFile.getId() + "/" + filename;
        String url = configuration.getString(SERVER_ADDRESS) + restPath + uploadedFile.getId() + "/" + uploadedFile.getName();
        uploadedFile.setPath(path);
        uploadedFile.setUrl(url);

        if (Objects.equals(extension, EBOOK_EXTENSION)) {
            unpackArchive(limitedSizeInputStream, path);
        } else {
            writeToFile(limitedSizeInputStream, path);
        }

        return update(uploadedFile);
    }
}
