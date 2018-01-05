package ee.hm.dop.service.statistics;

import ee.hm.dop.service.reviewmanagement.dto.StatisticsQuery;

import java.util.List;
import java.util.stream.Collectors;

public class StatisticsUtil {

    public static final String[] HEADERS = new String[]{
            "Kasutaja",
            "Valdkond",
            "Ülevaadatuks märgitud õppevara",
            "Sobivaks märgitud teatatud õppevara",
            "Kustutatuks märgitud teatatud õppevara",
            "Kinnitatud muudatustega õppevara",
            "Eemaldatud muudatustega õppevara",
            "Ebasobivaks/katkiseks märgitud õppevara",
            "Lisatud kogumikke",
            "Avalikustatud kogumikke",
            "Materjali lisamine"
    };

    public static List<StatisticsQuery> convertToStatisticsQuery(List<Object[]> userCountPairs) {
        return userCountPairs.stream()
                .map(object -> new StatisticsQuery((Long) object[0], (Long) object[1]))
                .collect(Collectors.toList());
    }
}
