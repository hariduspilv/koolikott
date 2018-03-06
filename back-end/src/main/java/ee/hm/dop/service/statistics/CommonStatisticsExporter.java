package ee.hm.dop.service.statistics;

import ee.hm.dop.dao.TranslationGroupDao;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.reviewmanagement.newdto.EducationalContextRow;
import ee.hm.dop.service.reviewmanagement.newdto.NewStatisticsResult;
import ee.hm.dop.service.reviewmanagement.newdto.NewStatisticsRow;
import ee.hm.dop.service.reviewmanagement.newdto.TranslationTaxon;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommonStatisticsExporter {

    @Inject
    private TranslationGroupDao translationGroupDao;

    public NewStatisticsResult translateAndSort(NewStatisticsResult statistics, Long estId) {
        setTranslations(statistics, estId);
        sort(statistics);
        return statistics;
    }

    private void setTranslations(NewStatisticsResult statistics, Long estId) {
        for (EducationalContextRow ecRow : statistics.getRows()) {
            for (NewStatisticsRow dRow : ecRow.getRows()) {
                dRow.setDomainTranslation(translationOrName(estId, dRow.getDomain()));
                if (dRow.getSubject() != null) {
                    dRow.setSubjectTranslation(translationOrName(estId, dRow.getSubject()));
                }
                for (NewStatisticsRow sRow : dRow.getSubjects()) {
                    sRow.setSubjectTranslation(translationOrName(estId, sRow.getSubject()));
                }
            }
        }
    }

    private void sort(NewStatisticsResult statistics) {
        for (EducationalContextRow ecRow : statistics.getRows()) {
            for (NewStatisticsRow dRow : ecRow.getRows()) {
                Collections.sort(dRow.getSubjects(), Comparator.comparing(NewStatisticsRow::getSubjectTranslation));
            }
            Collections.sort(ecRow.getRows(), Comparator.comparing(NewStatisticsRow::getDomainTranslation));
        }
    }

    public String[] taxonHeader(NewStatisticsResult statistics, Long estId) {
        String translationString = "";
        List<TranslationTaxon> translationTaxons = new ArrayList<>();
        for (EducationalContextRow ecRow : statistics.getRows()) {
            for (NewStatisticsRow dRow : ecRow.getRows()) {
                if (dRow.getSubject() == null) {
                    if (dRow.isDomainUsed() || dRow.isNoUsersFound()) {
                        TranslationTaxon taxon = new TranslationTaxon(dRow.getDomainTranslation(), dRow.getDomain());
                        if (!translationTaxons.contains(taxon)) {
                            translationTaxons.add(taxon);
                        }
                    }
                } else {
                    if (dRow.isDomainUsed() || dRow.isNoUsersFound()) {
                        TranslationTaxon taxon = new TranslationTaxon(dRow.getSubjectTranslation(), dRow.getDomain(), dRow.getSubject());
                        if (!translationTaxons.contains(taxon)) {
                            translationTaxons.add(taxon);
                        }
                    }
                }
                for (NewStatisticsRow sRow : dRow.getSubjects()) {
                    TranslationTaxon taxon = new TranslationTaxon(sRow.getSubjectTranslation(), sRow.getDomain(), sRow.getSubject());
                    if (!translationTaxons.contains(taxon)) {
                        translationTaxons.add(taxon);
                    }
                }
            }
        }
        for (int i = 0; i < translationTaxons.size(); i++) {
            TranslationTaxon translationTaxon = translationTaxons.get(i);
            String separator = pickSeparator(translationTaxons, i, translationTaxon);
            translationString = translationString + separator + translationTaxon.getTranslation();
        }
        String ecTranslation = translationOrName(estId, statistics.getRows().get(0).getEducationalContext());
        return StatisticsUtil.taxonHeader(statistics.getFilter().getFrom(), statistics.getFilter().getTo(), ecTranslation, translationTaxons.size() > 1, translationString);
    }

    private String pickSeparator(List<TranslationTaxon> rows, int i, TranslationTaxon currentRow) {
        TranslationTaxon previousRow = i != 0 ? rows.get(i - 1) : null;
        String separator;
        if (previousRow == null) {
            separator = "";
        } else if (previousRow.getDomain().getId().equals(currentRow.getDomain().getId())) {
            separator = ", ";
        } else {
            separator = "; ";
        }
        return separator;
    }

    public String translationOrName(Long estId, Taxon taxon) {
        String translationKey = StatisticsUtil.getTranslationKey(taxon);
        String translation = translationGroupDao.getTranslationByKeyAndLangcode(translationKey, estId);
        return translation != null ? translation : taxon.getName();
    }
}
