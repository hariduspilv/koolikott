package ee.hm.dop.service.statistics;

import com.opencsv.CSVWriter;
import ee.hm.dop.model.UserDto;
import ee.hm.dop.service.metadata.TranslationService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional
public class UserStatisticsExporter {

    private static final Logger logger = LoggerFactory.getLogger(UserStatisticsExporter.class);

    @Inject
    private TranslationService translationService;

    public void generateXlsx(String filename, List<UserDto> userDtos, String languageCode) {
        generateExcel(filename, userDtos, languageCode, new XSSFWorkbook());
    }

    public void generateXls(String filename, List<UserDto> userDtos, String languageCode) {
        generateExcel(filename, userDtos, languageCode, new HSSFWorkbook());
    }

    public void generateCsv(String filename, List<UserDto> userDtos, String languageCode) {
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filename, true), StandardCharsets.UTF_8), ';')) {
            List<String> headers = getTranslatedTableHeaders(languageCode);
            String[] headersArray = new String[headers.size()];
            headers.toArray(headersArray);
            writer.writeNext(headersArray);

            IntStream.range(0, userDtos.size())
                    .forEach(u -> {
                        String[] row = writeCsvRows(userDtos.get(u));
                        writer.writeNext(row);
                    });
        } catch (IOException ex) {
            logger.error("Users file generation failed");
        }
    }

    private void generateExcel(String filename, List<UserDto> userDtos, String languageCode, Workbook book) {
        Workbook workbook = createWorkBook(userDtos, languageCode, book);
        try (FileOutputStream outputStream = new FileOutputStream(filename)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            logger.error("Users file generation failed");
        }
    }

    private Workbook createWorkBook(List<UserDto> userDtos, String languageCode, Workbook book) {
        Sheet sheet = book.createSheet("E-koolikoti_kasutajad");
        Row headers = sheet.createRow(0);
        List<String> headerStrings = getTranslatedTableHeaders(languageCode);
        IntStream.range(0, headerStrings.size()).forEach(i -> {
            Cell cell = headers.createCell(i);
            cell.setCellValue(headerStrings.get(i));
        });
        IntStream.range(0, userDtos.size())
                .forEach(u -> {
                    Row row = sheet.createRow(u + 1);
                    setExcelCells(userDtos.get(u), row);
                });
        return book;
    }

    private void setExcelCells(UserDto user, Row row) {

        Cell cell0 = row.createCell(0);
        if (user.firstName != null && user.lastName != null) {
            String fullName = user.firstName + " " + user.lastName;
            cell0.setCellValue(fullName);
        }

        Cell cell1 = row.createCell(1);
        if (user.email != null && user.emailActivated) {
            cell1.setCellValue(user.email);
        }

        Cell cell2 = row.createCell(2);
        if (user.applicationRole != null) {
            cell2.setCellValue(user.applicationRole);
        }

        Cell cell3 = row.createCell(3);
        if (user.userProfileRole != null) {
            cell3.setCellValue(getUserRole(user));
        }

        Cell cell4 = row.createCell(4);
        if (user.educationalContexts != null) {
            cell4.setCellValue(getUserTaxons(user.educationalContexts));
        }

        Cell cell6 = row.createCell(5);
        if (user.domains != null) {
            cell6.setCellValue(getUserTaxons(user.domains));
        }

        Cell cell7 = row.createCell(6);
        if (user.lastLogin != null) {
            cell7.setCellValue(getUserLastLogin(user.lastLogin));
        }
    }

    private String[] writeCsvRows(UserDto user) {
        List<String> row = new ArrayList<>();

        if (user.firstName != null && user.lastName != null) {
            String fullName = user.firstName + " " + user.lastName;
            row.add(fullName);
        } else {
            row.add("");
        }

        if (user.email != null && user.emailActivated) {
            row.add(user.email);
        } else {
            row.add("");
        }

        if (user.applicationRole != null) {
            row.add(user.applicationRole);
        } else {
            row.add("");
        }

        if (user.userProfileRole != null) {
            row.add(getUserRole(user));
        } else {
            row.add("");
        }

        if (user.educationalContexts != null) {
            row.add(getUserTaxons(user.educationalContexts));
        } else {
            row.add("");
        }

        if (user.domains != null) {
            row.add(getUserTaxons(user.domains));
        } else {
            row.add("");
        }

        if (user.lastLogin != null) {
            row.add(getUserLastLogin(user.lastLogin));
        } else {
            row.add("");
        }
        String[] rowArray = new String[row.size()];
        return row.toArray(rowArray);
    }

    private String getUserTaxons(List<String> taxons) {
        return String.join(", ", taxons);
    }

    private String getUserRole(UserDto user) {
        String userProfileRole = user.userProfileRole;
        if (user.customRole != null && !user.customRole.equals("")) {
            userProfileRole += ": " + user.customRole;
        }
        return userProfileRole;
    }

    private List<String> getTranslatedTableHeaders(String languageKey) {
        List<String> headerTranslationKeys = StatisticsUtil.USER_TABLE_HEADERS;
        List<String> translatedHeaders = new ArrayList<>();
        for (String headerTranslationKey : headerTranslationKeys) {
            translatedHeaders.add(translationService.getTranslations(headerTranslationKey, languageKey));
        }
        return translatedHeaders;
    }

    private String getUserLastLogin(Timestamp timestamp) {
        if (timestamp != null) {
            LocalDate lastLoginDate = timestamp.toInstant().atZone(ZoneId.of("Europe/Tallinn")).toLocalDate();
            return getFormattedDate(lastLoginDate);
        } else {
            return null;
        }
    }

    private String getFormattedDate(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
