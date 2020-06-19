package ee.hm.dop.service;

import ee.hm.dop.dao.LanguageDao;
import ee.hm.dop.dao.UserDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserDto;
import ee.hm.dop.model.administration.PageableQueryUsers;
import ee.hm.dop.service.reviewmanagement.dto.FileFormat;
import ee.hm.dop.service.statistics.UserStatisticsExporter;
import ee.hm.dop.service.useractions.UserService;
import ee.hm.dop.utils.io.CsvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserStatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(UserStatisticsService.class);

    @Inject
    private UserStatisticsExporter userStatisticsExporter;
    @Inject
    private UserDao userDao;
    @Inject
    private UserService userService;
    @Inject
    private LanguageDao languageDao;

    public String generateFile(String languageCode, String fileType, PageableQueryUsers pageableQuery, User loggedInUser) {
        FileFormat fileFormat = getFileFormat(fileType);
        String filename = getFileName(fileFormat);
        List<UserDto> userDtoList = userService.getUserDtos(pageableQuery);

        if (fileFormat == FileFormat.xlsx) {
            userStatisticsExporter.generateXlsx(filename, userDtoList, languageCode);
        } else if (fileFormat == FileFormat.xls) {
            userStatisticsExporter.generateXls(filename, userDtoList, languageCode);
        } else if (fileFormat == FileFormat.csv) {
            userStatisticsExporter.generateCsv(filename, userDtoList, languageCode);
        }
        else {
            throw new IllegalStateException("FileFormat is in unknown state: " + fileFormat.name());
        }
        logger.info(String.format("User statistics file downloaded at: %s by userId: %d, username: %s", LocalDateTime.now(), loggedInUser.getId(), loggedInUser.getUsername()));
        return filename;
    }

    private String getFileName(FileFormat fileFormat) {
        return CsvUtil.getUniqueFileName(fileFormat);
    }

    private FileFormat getFileFormat(String fileType) {
        if (fileType.equals(FileFormat.xlsx.name())) {
            return FileFormat.xlsx;
        } else if (fileType.equals(FileFormat.xls.name())) {
            return FileFormat.xls;
        } else if (fileType.equals(FileFormat.csv.name())) {
            return FileFormat.csv;
        } else {
            throw new IllegalStateException("FileFormat is unknown: " + fileType);
        }
    }
}
