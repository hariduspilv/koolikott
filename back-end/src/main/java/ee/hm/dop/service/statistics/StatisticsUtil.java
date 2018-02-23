package ee.hm.dop.service.statistics;

import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsQuery;
import ee.hm.dop.utils.DateUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.stream.Collectors;

public class StatisticsUtil {




    public static final String[] USER_HEADERS = new String[]{
            "Haridustase",
            "Valdkond",
            "Alamvaldkond",
            "Ülevaadatuks märgitud õppevara",
            "Sobivaks märgitud teatatud õppevara",
            "Kustutatuks märgitud teatatud õppevara",
            "Kinnitatud muudatustega õppevara",
            "Eemaldatud muudatustega õppevara",
            "Lisatud kogumikke",
            "Avalikustatud kogumikke",
            "Lisatud materjale"
    };

    public static final String[] TAXON_HEADERS = new String[]{
            "Haridustase",
            "Valdkond",
            "Alamvaldkond",
            "Kasutaja",
            "Ülevaadatuks märgitud õppevara",
            "Sobivaks märgitud teatatud õppevara",
            "Kustutatuks märgitud teatatud õppevara",
            "Kinnitatud muudatustega õppevara",
            "Eemaldatud muudatustega õppevara",
            "Lisatud kogumikke",
            "Avalikustatud kogumikke",
            "Lisatud materjale"
    };
    public static final String NO_USER_FOUND = "Ei leitud ühtegi kasutajat";
    public static final String EMPTY_ROW = "-";

    public static String[] userHeader(DateTime from, DateTime to, User user) {
        return new String[]{
                "Alates",
                DateUtils.toString_ddMMyyyy(from),
                "Kuni",
                DateUtils.toString_ddMMyyyy(to),
                "Kasutaja",
                user.getFullName()
        };
    }

    public static String[] taxonHeader(DateTime from, DateTime to, String educationalContext, boolean plural, String others) {
        return new String[]{
                "Alates",
                DateUtils.toString_ddMMyyyy(from),
                "Kuni",
                DateUtils.toString_ddMMyyyy(to),
                "Haridus",
                educationalContext,
                plural ? "Ainevaldkonnad" : "Ainevaldkond",
                others
        };
    }

    public static List<StatisticsQuery> convertToStatisticsQuery(List<Object[]> userCountPairs) {
        return userCountPairs.stream()
                .map(object -> new StatisticsQuery((Long) object[0], (Long) object[1]))
                .collect(Collectors.toList());
    }

    public static String getTranslationKey(Taxon taxon) {
        if (taxon instanceof EducationalContext){
            return taxon.getName().toUpperCase();
        }
        return taxon.getTaxonLevel().toUpperCase() + "_" + taxon.getName().toUpperCase();
    }
}
