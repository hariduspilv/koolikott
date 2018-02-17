package ee.hm.dop.service.statistics;

import com.opencsv.CSVWriter;
import ee.hm.dop.dao.LanguageDao;
import ee.hm.dop.dao.TranslationGroupDao;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsResult;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsRow;
import ee.hm.dop.service.reviewmanagement.dto.UserStatistics;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatisticsCsvExporter {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsCsvExporter.class);

    @Inject
    private TranslationGroupDao translationGroupDao;
    @Inject
    private LanguageDao languageDao;

    public void generate(String fileName, StatisticsResult statistics) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeNext(StatisticsUtil.USER_HEADERS);

            for (UserStatistics s : statistics.getRows()) {
                List<StatisticsRow> rows = s.getRows();
                for (int i = 0; i < rows.size(); i++) {
                    StatisticsRow row = rows.get(i);
                    writer.writeNext(generateRow(row, i == 0, null));
                    if (CollectionUtils.isNotEmpty(row.getSubjects())){
                        for (StatisticsRow childRow : row.getSubjects()) {
                            writer.writeNext(generateRow(childRow, false, null));
                        }
                    }
                }

            }
            writer.writeNext(generateRow(statistics.getSum(), false, "Kokku"));
        } catch (IOException ex) {
            logger.error(statistics.getFilter().getFormat().name() + " file generation failed");
        }
    }

    private String[] generateRow(StatisticsRow s, boolean firstRow, String name) {
        Long estId = languageDao.findByCode("et").getId();
        String username = !firstRow || s.getUser() == null ? "" : s.getUser().getUsername();
        String userTaxons = name != null ? name : s.getUsertaxon() != null ? joinTranslation(s, estId) : "";
        return new String[]{
                username,
                userTaxons,
                s.getReviewedLOCount().toString(),
                s.getApprovedReportedLOCount().toString(),
                s.getDeletedReportedLOCount().toString(),
                s.getAcceptedChangedLOCount().toString(),
                s.getRejectedChangedLOCount().toString(),
                s.getReportedLOCount().toString(),
                s.getPortfolioCount().toString(),
                s.getPublicPortfolioCount().toString(),
                s.getMaterialCount().toString()
        };
    }

    private String joinTranslation(StatisticsRow s, Long langCode) {
        return Stream.of(s.getUsertaxon())
                .map(t -> t.getLevel().toUpperCase() + "_" + t.getName().toUpperCase())
                .map(tk -> translationGroupDao.getTranslationByKeyAndLangcode(tk, langCode))
                .collect(Collectors.joining(", "));
    }
}
