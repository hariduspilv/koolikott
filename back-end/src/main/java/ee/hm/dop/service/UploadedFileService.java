package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.DOCUMENT_MAX_FILE_SIZE;
import static ee.hm.dop.utils.ConfigurationProperties.FILE_UPLOAD_DIRECTORY;
import static ee.hm.dop.utils.ConfigurationProperties.SERVER_ADDRESS;
import static ee.hm.dop.utils.DOPFileUtils.writeToFile;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.dao.UploadedFileDAO;
import ee.hm.dop.io.LimitedSizeInputStream;
import ee.hm.dop.model.UploadedFile;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

public class UploadedFileService {

    public static final String REST_UPLOADED_FILE = "/rest/uploadedFile/";

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

    public Response getFile(Long fileId, String filename) {
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
        return Response.ok(FileUtils.getFile(file.getPath()), mediaType)
                .header("Content-Disposition", "Inline; filename=\"" + filename + "\"")
                .build();
    }

    public UploadedFile uploadFile(InputStream fileInputStream, FormDataContentDisposition fileDetail, String rootPath, String restPath) throws UnsupportedEncodingException {
        LimitedSizeInputStream limitedSizeInputStream = new LimitedSizeInputStream(configuration.getInt(DOCUMENT_MAX_FILE_SIZE), fileInputStream);

        UploadedFile uploadedFile = new UploadedFile();
        String filename = URLEncoder.encode(fileDetail.getFileName(), UTF_8.name());
        uploadedFile.setName(filename);
        uploadedFile = create(uploadedFile);

        //After creating uploaded file object to DB, upload file to server
        String path = rootPath + uploadedFile.getId() + "/" + filename;
        String url = configuration.getString(SERVER_ADDRESS) + restPath + uploadedFile.getId() + "/" + uploadedFile.getName();
        uploadedFile.setPath(path);
        uploadedFile.setUrl(url);

        writeToFile(limitedSizeInputStream, path);
        return update(uploadedFile);
    }
}
