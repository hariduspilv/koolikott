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

import static ee.hm.dop.service.statistics.StatisticsUtil.EMPTY_ROW;
import static ee.hm.dop.service.statistics.StatisticsUtil.NO_USER_FOUND;

public class NewStatisticsCsvExporter {

    private static final Logger logger = LoggerFactory.getLogger(NewStatisticsCsvExporter.class);
    @Inject
    private TranslationGroupDao translationGroupDao;
    @Inject
    private LanguageDao languageDao;

    public void generate(String filename, NewStatisticsResult statistics) {
        Long estId = languageDao.findByCode("et").getId();
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            if (statistics.getFilter().isUserSearch()) {
                writer.writeNext(StatisticsUtil.userHeader(statistics));
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
            } else {
                writer.writeNext(StatisticsUtil.TAXON_HEADERS);
                for (EducationalContextRow s : statistics.getRows()) {
                    List<NewStatisticsRow> rows = s.getRows();
                    for (NewStatisticsRow row : rows) {
                        if (row.isNoUsersFound()){
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
        } catch (IOException ex) {
            logger.error(statistics.getFilter().getFormat().name() + " file generation failed");
        }
    }

    private String[] generateNoUserFoundRow(Long estId, NewStatisticsRow row) {
        return new String[]{
                translationOrName(estId, row.getEducationalContext()),
                translationOrName(estId, row.getDomain()),
                row.getSubject() == null ? "" : translationOrName(estId, row.getSubject()),
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
                sumRow != null ? "" : translationOrName(estId, s.getEducationalContext()),
                sumRow != null ? "" : translationOrName(estId, s.getDomain()),
                sumRow != null ? sumRow : s.getSubject() == null ? "" : translationOrName(estId, s.getSubject()),
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
                sumRow != null ? "" : translationOrName(estId, s.getEducationalContext()),
                sumRow != null ? "" : translationOrName(estId, s.getDomain()),
                sumRow != null ? "" : s.getSubject() == null ? "" : translationOrName(estId, s.getSubject()),
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

    private String translationOrName(Long estId, Taxon taxon) {
        String translationKey = getTranslationKey(taxon);
        String translation = translationGroupDao.getTranslationByKeyAndLangcode(translationKey, estId);
        return translation != null ? translation : taxon.getName();
    }

    private String getTranslationKey(Taxon taxon) {
        return taxon.getTaxonLevel().toUpperCase() + "_" + taxon.getName().toUpperCase();
    }
}
