package ee.hm.dop.service.statistics;

import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsQuery;
import ee.hm.dop.utils.DateUtils;

import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsUtil {

    static final String[] USER_HEADERS = new String[]{
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

    static final String[] TAXON_HEADERS = new String[]{
            "Haridustase",
            "Valdkond",
            "Alamvaldkond",
            "Ekspert",
            "Ülevaadatuks märgitud õppevara",
            "Sobivaks märgitud teatatud õppevara",
            "Kustutatuks märgitud teatatud õppevara",
            "Kinnitatud muudatustega õppevara",
            "Eemaldatud muudatustega õppevara",
            "Lisatud kogumikke",
            "Avalikustatud kogumikke",
            "Lisatud materjale"
    };
    static final String NO_USER_FOUND = "Ei leitud ühtegi eksperti";
    static final String EMPTY_ROW = "-";

    public static String[] userHeader(LocalDateTime from, LocalDateTime to, User user) {
        return new String[]{
                "Alates",
                DateUtils.toString_ddMMyyyy(from),
                "Kuni",
                DateUtils.toString_ddMMyyyy(to),
                "Ekspert",
                user.getFullName()
        };
    }

    public static String[] taxonHeader(LocalDateTime from, LocalDateTime to, String educationalContext, boolean plural, String others) {
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
                .map(object -> new StatisticsQuery(bigDecimalToLong(object[0]), bigDecimalToLong(object[1])))
                .collect(Collectors.toList());
    }

    public static String getTranslationKey(Taxon taxon) {
        if (taxon instanceof EducationalContext) {
            return taxon.getName().toUpperCase();
        }
        return taxon.getTaxonLevel().toUpperCase() + "_" + taxon.getName().toUpperCase();
    }

    private static Long bigDecimalToLong(Object o) {
        return new BigDecimal(o.toString()).longValue();
    }
}
