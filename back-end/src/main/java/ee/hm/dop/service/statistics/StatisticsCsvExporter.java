package ee.hm.dop.service.statistics;

import com.opencsv.CSVWriter;
import ee.hm.dop.dao.LanguageDao;
import ee.hm.dop.dao.TranslationGroupDao;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsResult;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class StatisticsCsvExporter {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsCsvExporter.class);

    @Inject
    private TranslationGroupDao translationGroupDao;
    @Inject
    private LanguageDao languageDao;

    public void generate(String fileName, StatisticsResult statistics) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeNext(StatisticsUtil.HEADERS);

            for (StatisticsRow s : statistics.getRows()) {
                writer.writeNext(generateRow(s));
            }
            writer.writeNext(generateRow(statistics.getSum()));
        } catch (IOException ex) {
            logger.error(statistics.getFilter().getFormat().name() + " file generation failed");
        }
    }

    private String[] generateRow(StatisticsRow s) {
        Long estId = languageDao.findByCode("et").getId();
        String username = s.getUser() != null ? s.getUser().getUsername() : "";
        String userTaxons = isNotEmpty(s.getUsertaxons()) ? joinTranslation(s, estId) : "";
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
        return s.getUsertaxons().stream()
                .map(t -> t.getLevel().toUpperCase() + "_" + t.getName().toUpperCase())
                .map(tk -> translationGroupDao.getTranslationByKeyAndLangcode(tk, langCode))
                .collect(Collectors.joining(", "));
    }
}
