package ee.hm.dop.service.statistics;

import ee.hm.dop.dao.LanguageDao;
import ee.hm.dop.dao.TranslationGroupDao;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsResult;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsRow;
import ee.hm.dop.service.reviewmanagement.dto.UserStatistics;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatisticsExcelExporter {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsExcelExporter.class);

    @Inject
    private TranslationGroupDao translationGroupDao;
    @Inject
    private LanguageDao languageDao;

    public void generate(String fileName, StatisticsResult statistics) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Statistika aruanne");

        int rowNum = 0;
        int xlsColNum = 0;
        Row headersRow = sheet.createRow(rowNum++);
        for (String heading : StatisticsUtil.HEADERS) {
            Cell cell = headersRow.createCell(xlsColNum++);
            cell.setCellValue(heading);
        }

        List<UserStatistics> rows = statistics.getRows();
        for (UserStatistics s : rows) {
            List<StatisticsRow> rows1 = s.getRows();
            for (int i = 0; i < rows1.size(); i++) {
                StatisticsRow row = rows1.get(i);
                rowNum = writeRow(sheet, rowNum, row, i == 0, null);
            }
        }
        writeRow(sheet, rowNum, statistics.getSum(), false, "Kokku");


        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            logger.error(statistics.getFilter().getFormat().name() + " file generation failed");
        }
    }

    private int writeRow(XSSFSheet sheet, int rowNum, StatisticsRow s, boolean firstRow, String name) {
        int xlsColNum;
        xlsColNum = 0;
        Row row = sheet.createRow(rowNum++);
        Cell cell = row.createCell(xlsColNum++);
        if (!firstRow) {
            cell.setCellValue("");
        } else {
            if (s.getUser() != null) {
                cell.setCellValue(s.getUser().getUsername());
            } else {
                cell.setCellValue("");
            }
        }
        cell = row.createCell(xlsColNum++);
        if (name != null) {
            cell.setCellValue(name);
        } else {
            if (s.getUsertaxon() == null) {
                cell.setCellValue("");
            } else {
                Long estId = languageDao.findByCode("et").getId();
                cell.setCellValue(joinTranslation(s, estId));
            }
        }
        cell = row.createCell(xlsColNum++);
        cell.setCellValue(s.getReviewedLOCount());
        cell = row.createCell(xlsColNum++);
        cell.setCellValue(s.getApprovedReportedLOCount());
        cell = row.createCell(xlsColNum++);
        cell.setCellValue(s.getDeletedReportedLOCount());
        cell = row.createCell(xlsColNum++);
        cell.setCellValue(s.getAcceptedChangedLOCount());
        cell = row.createCell(xlsColNum++);
        cell.setCellValue(s.getRejectedChangedLOCount());
        cell = row.createCell(xlsColNum++);
        cell.setCellValue(s.getReportedLOCount());
        cell = row.createCell(xlsColNum++);
        cell.setCellValue(s.getPortfolioCount());
        cell = row.createCell(xlsColNum++);
        cell.setCellValue(s.getPublicPortfolioCount());
        cell = row.createCell(xlsColNum++);
        cell.setCellValue(s.getMaterialCount());
        return rowNum;
    }

    private String joinTranslation(StatisticsRow s, Long langCode) {
        return Stream.of(s.getUsertaxon())
                .map(t -> t.getLevel().toUpperCase() + "_" + t.getName().toUpperCase())
                .map(tk -> translationGroupDao.getTranslationByKeyAndLangcode(tk, langCode))
                .collect(Collectors.joining(", "));
    }
}
