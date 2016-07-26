package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.FILE_UPLOAD_DIRECTORY;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.dao.UploadedFileDAO;
import ee.hm.dop.model.UploadedFile;
import ee.hm.dop.utils.DOPFileUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadedFileService {

    @Inject
    private UploadedFileDAO uploadedFileDAO;

    @Inject
    private Configuration configuration;

    private static Logger logger = LoggerFactory.getLogger(UploadedFileService.class);

    private UploadedFile getUploadedFileById(Long id) {
        return uploadedFileDAO.findUploadedFileById(id);
    }

    private UploadedFile update(UploadedFile uploadedFile){
        return uploadedFileDAO.update(uploadedFile);
    }

    private UploadedFile create(UploadedFile uploadedFile) {
        return uploadedFileDAO.update(uploadedFile);
    }

    public Response getFile(Long fileId){
        UploadedFile file = getUploadedFileById(fileId);
        return Response.ok(FileUtils.getFile(file.getPath()), MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"" ) //optional
                    .build();
    }

    public UploadedFile uploadFile(InputStream fileInputStream, FormDataContentDisposition fileDetail){
        String filename = null;
        try {
            filename = URLEncoder.encode(fileDetail.getFileName(), UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            logger.warn("Could not URLEncode the file path");
        }
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setName(filename);
        UploadedFile newUploadedFile = create(uploadedFile);
        String path = configuration.getString(FILE_UPLOAD_DIRECTORY) +  newUploadedFile.getId() + "/" + filename;
        newUploadedFile.setPath(path);
        update(newUploadedFile);
        DOPFileUtils.writeToFile(fileInputStream, path);
        return newUploadedFile;
    }
}
