package ee.hm.dop.service.statistics;

import ee.hm.dop.service.reviewmanagement.dto.StatisticsQuery;
import ee.hm.dop.service.reviewmanagement.newdto.NewStatisticsResult;

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

    public static String[] userHeader(NewStatisticsResult statistics) {
        return new String[]{
                "Kasutaja",
                statistics.getFilter().getUsers().get(0).getFullName()
        };
    }

    public static List<StatisticsQuery> convertToStatisticsQuery(List<Object[]> userCountPairs) {
        return userCountPairs.stream()
                .map(object -> new StatisticsQuery((Long) object[0], (Long) object[1]))
                .collect(Collectors.toList());
    }
}
