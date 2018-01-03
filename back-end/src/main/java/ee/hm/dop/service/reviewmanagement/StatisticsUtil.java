package ee.hm.dop.service.reviewmanagement;

import ee.hm.dop.service.reviewmanagement.dto.StatisticsQuery;

import java.util.List;
import java.util.stream.Collectors;

public class StatisticsUtil {

    public static List<StatisticsQuery> convertToStatisticsQuery(List<Object[]> userCountPairs) {
        return userCountPairs.stream()
                .map(object -> new StatisticsQuery((Long) object[0], (Long) object[1]))
                .collect(Collectors.toList());
    }
}
