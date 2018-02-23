package ee.hm.dop.service.statistics;

import ee.hm.dop.dao.TranslationGroupDao;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.reviewmanagement.newdto.NewStatisticsResult;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class CommonStatisticsExporter {

    @Inject
    private TranslationGroupDao translationGroupDao;

    public String[] taxonHeader(NewStatisticsResult statistics, Long estId) {
        List<Taxon> taxons = statistics.getDbTaxons();
        String ecTranslation = translationOrName(estId, statistics.getRows().get(0).getEducationalContext());
        String searchTaxons = joinTranslation(estId, taxons);
        return StatisticsUtil.taxonHeader(statistics.getFilter().getFrom(), statistics.getFilter().getTo(), ecTranslation, taxons.size() > 1, searchTaxons);
    }

    public String translationOrName(Long estId, Taxon taxon) {
        String translationKey = StatisticsUtil.getTranslationKey(taxon);
        String translation = translationGroupDao.getTranslationByKeyAndLangcode(translationKey, estId);
        return translation != null ? translation : taxon.getName();
    }

    public String joinTranslation(Long langId, List<Taxon> taxons) {
        return taxons.stream()
                .map(t -> this.translationOrName(langId, t))
                .collect(Collectors.joining(", "));
    }
}
