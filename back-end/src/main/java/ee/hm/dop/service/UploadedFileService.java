package ee.hm.dop.service;

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
import ee.hm.dop.model.UploadedFile;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

public class UploadedFileService {

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

    public UploadedFile uploadFile(InputStream fileInputStream, FormDataContentDisposition fileDetail) throws UnsupportedEncodingException {
        String filename;
        filename = URLEncoder.encode(fileDetail.getFileName(), UTF_8.name());
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setName(filename);
        UploadedFile newUploadedFile = create(uploadedFile);
        String url = configuration.getString(SERVER_ADDRESS) + "/rest/uploadedFile/" + newUploadedFile.getId() + "/" + newUploadedFile.getName();
        String path = configuration.getString(FILE_UPLOAD_DIRECTORY) + newUploadedFile.getId() + "/" + filename;
        newUploadedFile.setPath(path);
        newUploadedFile.setUrl(url);
        update(newUploadedFile);
        writeToFile(fileInputStream, path);
        return newUploadedFile;
    }
}
