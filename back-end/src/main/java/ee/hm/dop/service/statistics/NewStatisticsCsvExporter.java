package ee.hm.dop.service.statistics;

import com.opencsv.CSVWriter;
import ee.hm.dop.dao.LanguageDao;
import ee.hm.dop.dao.UserDao;
import ee.hm.dop.model.User;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.newdto.EducationalContextRow;
import ee.hm.dop.service.reviewmanagement.newdto.NewStatisticsResult;
import ee.hm.dop.service.reviewmanagement.newdto.NewStatisticsRow;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static ee.hm.dop.service.statistics.StatisticsUtil.EMPTY_ROW;
import static ee.hm.dop.service.statistics.StatisticsUtil.NO_USER_FOUND;

public class NewStatisticsCsvExporter {

    private static final Logger logger = LoggerFactory.getLogger(NewStatisticsCsvExporter.class);
    @Inject
    private LanguageDao languageDao;
    @Inject
    private UserDao userDao;
    @Inject
    private CommonStatisticsExporter commonStatisticsExporter;

    public void generate(String filename, NewStatisticsResult statistics) {
        Long estId = languageDao.findByCode("et").getId();
        NewStatisticsResult sortedStatistics = commonStatisticsExporter.translateAndSort(statistics, estId);
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filename, true), StandardCharsets.UTF_8))) {
            if (sortedStatistics.getFilter().isUserSearch()) {
                userCsv(sortedStatistics, estId, writer);
            } else {
                taxonCsv(sortedStatistics, estId, writer);
            }
        } catch (IOException ex) {
            logger.error(statistics.getFilter().getFormat().name() + " file generation failed");
        }
    }

    private void taxonCsv(NewStatisticsResult statistics, Long estId, CSVWriter writer) {
        writer.writeNext(commonStatisticsExporter.taxonHeader(statistics, estId));
        writer.writeNext(StatisticsUtil.TAXON_HEADERS);
        for (EducationalContextRow s : statistics.getRows()) {
            List<NewStatisticsRow> rows = s.getRows();
            for (NewStatisticsRow row : rows) {
                if (row.isNoUsersFound()) {
                    writer.writeNext(generateNoUserFoundRow(estId, row));
                }
                if (row.isDomainUsed()) {
                    writer.writeNext(generateTaxonRow(row, null, estId));
                }
                if (CollectionUtils.isNotEmpty(row.getSubjects())) {
                    for (NewStatisticsRow childRow : row.getSubjects()) {
                        writer.writeNext(generateTaxonRow(childRow, null, estId));
                    }
                }
            }
        }
        writer.writeNext(generateTaxonRow(statistics.getSum(), "Kokku", estId));
    }

    private void userCsv(NewStatisticsResult statistics, Long estId, CSVWriter writer) {
        StatisticsFilterDto filter = statistics.getFilter();
        User userDto = filter.getUsers().get(0);
        User user = userDao.findById(userDto.getId());
        writer.writeNext(StatisticsUtil.userHeader(filter.getFrom(), filter.getTo(), user));
        writer.writeNext(StatisticsUtil.USER_HEADERS);
        for (EducationalContextRow s : statistics.getRows()) {
            List<NewStatisticsRow> rows = s.getRows();
            for (NewStatisticsRow row : rows) {
                if (row.isDomainUsed()) {
                    writer.writeNext(generateUserRow(row, null, estId));
                }
                if (CollectionUtils.isNotEmpty(row.getSubjects())) {
                    for (NewStatisticsRow childRow : row.getSubjects()) {
                        writer.writeNext(generateUserRow(childRow, null, estId));
                    }
                }
            }
        }
        writer.writeNext(generateUserRow(statistics.getSum(), "Kokku", estId));
    }

    private String[] generateNoUserFoundRow(Long estId, NewStatisticsRow row) {
        return new String[]{
                commonStatisticsExporter.translationOrName(estId, row.getEducationalContext()),
                commonStatisticsExporter.translationOrName(estId, row.getDomain()),
                row.getSubject() == null ? "" : commonStatisticsExporter.translationOrName(estId, row.getSubject()),
                NO_USER_FOUND,
                EMPTY_ROW,
                EMPTY_ROW,
                EMPTY_ROW,
                EMPTY_ROW,
                EMPTY_ROW,
                EMPTY_ROW,
                EMPTY_ROW,
                EMPTY_ROW
        };
    }

    private String[] generateUserRow(NewStatisticsRow s, String sumRow, Long estId) {
        return new String[]{
                sumRow != null ? "" : commonStatisticsExporter.translationOrName(estId, s.getEducationalContext()),
                sumRow != null ? "" : commonStatisticsExporter.translationOrName(estId, s.getDomain()),
                sumRow != null ? sumRow : s.getSubject() == null ? "" : commonStatisticsExporter.translationOrName(estId, s.getSubject()),
                s.getReviewedLOCount().toString(),
                s.getApprovedReportedLOCount().toString(),
                s.getDeletedReportedLOCount().toString(),
                s.getAcceptedChangedLOCount().toString(),
                s.getRejectedChangedLOCount().toString(),
                s.getPortfolioCount().toString(),
                s.getPublicPortfolioCount().toString(),
                s.getMaterialCount().toString()
        };
    }

    private String[] generateTaxonRow(NewStatisticsRow s, String sumRow, Long estId) {
        return new String[]{
                sumRow != null ? "" : commonStatisticsExporter.translationOrName(estId, s.getEducationalContext()),
                sumRow != null ? "" : commonStatisticsExporter.translationOrName(estId, s.getDomain()),
                sumRow != null ? "" : s.getSubject() == null ? "" : commonStatisticsExporter.translationOrName(estId, s.getSubject()),
                sumRow != null ? sumRow : s.getUser().getFullName(),
                s.getReviewedLOCount().toString(),
                s.getApprovedReportedLOCount().toString(),
                s.getDeletedReportedLOCount().toString(),
                s.getAcceptedChangedLOCount().toString(),
                s.getRejectedChangedLOCount().toString(),
                s.getPortfolioCount().toString(),
                s.getPublicPortfolioCount().toString(),
                s.getMaterialCount().toString()
        };
    }
}
