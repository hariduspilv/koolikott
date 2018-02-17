package ee.hm.dop.service.statistics;

import com.opencsv.CSVWriter;
import ee.hm.dop.dao.LanguageDao;
import ee.hm.dop.dao.TranslationGroupDao;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.reviewmanagement.newdto.EducationalContextRow;
import ee.hm.dop.service.reviewmanagement.newdto.NewStatisticsResult;
import ee.hm.dop.service.reviewmanagement.newdto.NewStatisticsRow;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class NewStatisticsCsvExporter {

    private static final Logger logger = LoggerFactory.getLogger(NewStatisticsCsvExporter.class);
    @Inject
    private TranslationGroupDao translationGroupDao;
    @Inject
    private LanguageDao languageDao;

    public void generate(String filename, NewStatisticsResult statistics) {
        Long estId = languageDao.findByCode("et").getId();
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            writer.writeNext(StatisticsUtil.HEADERS);

            if (statistics.getFilter().isUserSearch()) {
                for (EducationalContextRow s : statistics.getRows()) {
                    List<NewStatisticsRow> rows = s.getRows();
                    for (int i = 0; i < rows.size(); i++) {
                        NewStatisticsRow row = rows.get(i);
                        if (row.isDomainUsed()) {
                            writer.writeNext(generateRow(row, null, estId));
                        }
                        if (CollectionUtils.isNotEmpty(row.getSubjects())) {
                            for (NewStatisticsRow childRow : row.getSubjects()) {
                                writer.writeNext(generateRow(childRow, null, estId));
                            }
                        }
                    }

                }
            }
            writer.writeNext(generateRow(statistics.getSum(), "Kokku", estId));
        } catch (IOException ex) {
            logger.error(statistics.getFilter().getFormat().name() + " file generation failed");
        }
    }

    private String[] generateRow(NewStatisticsRow s, String nameOfTheRow, Long estId) {
        return new String[]{
                translationGroupDao.getTranslationByKeyAndLangcode(getTranslationKey(s.getEducationalContext()), estId),
                translationGroupDao.getTranslationByKeyAndLangcode(getTranslationKey(s.getDomain()), estId),
                s.getSubject() == null ? "" : translationGroupDao.getTranslationByKeyAndLangcode(getTranslationKey(s.getSubject()), estId),
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

    private String getTranslationKey(Taxon taxon) {
        return taxon.getLevel().toUpperCase() + "_" + taxon.getName().toUpperCase();
    }
}
