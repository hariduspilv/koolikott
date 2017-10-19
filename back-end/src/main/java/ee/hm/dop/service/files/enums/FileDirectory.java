package ee.hm.dop.service.files.enums;

import ee.hm.dop.utils.ConfigurationProperties;

public enum FileDirectory {
    REVIEW(ConfigurationProperties.FILE_REVIEW_DIRECTORY) , UPDATE(ConfigurationProperties.FILE_UPLOAD_DIRECTORY);

    private String directory;

    FileDirectory(String fileReviewDirectory) {
        directory = fileReviewDirectory;
    }

    public String getDirectory() {
        return directory;
    }
}
