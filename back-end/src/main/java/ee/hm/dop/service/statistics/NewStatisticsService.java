package ee.hm.dop.service.statistics;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.dao.UserDao;
import ee.hm.dop.dao.specialized.StatisticsDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.newdto.*;
import ee.hm.dop.utils.UserUtil;
import org.apache.commons.collections.CollectionUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NewStatisticsService {

    @Inject
    private UserDao userDao;
    @Inject
    private TaxonDao taxonDao;
    @Inject
    private StatisticsDao statisticsDao;
    @Inject
    private NewStatisticsByUserRequestBuilder userRequestBuilder;
    @Inject
    private NewStatisticsByUserRowCreator userRowCreator;
    @Inject
    private NewStatisticsRowSummer rowSummer;
    @Inject
    private NewStatisticsTaxonRequestBuilder taxonRequestBuilder;
    @Inject
    private NewTaxonRowCreator taxonRowCreator;

    public NewStatisticsResult statistics(StatisticsFilterDto filter, User loggedInUser) {
        UserUtil.mustBeAdmin(loggedInUser);

        if (filter.isUserSearch()) {
            return userSearchPath(filter);
        }
        return taxonSearchPath(filter);
    }

    private NewStatisticsResult userSearchPath(StatisticsFilterDto filter) {
        List<DomainWithChildren> domainsWithChildren = userRequestBuilder.userPath(filter);
        List<NewStatisticsRow> rows = userRowCreator.createRows(filter, filter.getUsers().get(0), domainsWithChildren);
        return new NewStatisticsResult(filter, convertECRows(groupByEC(rows)), rowSummer.getSum(rows));
    }

    private NewStatisticsResult taxonSearchPath(StatisticsFilterDto filter) {
        List<TaxonAndUserRequest> taxonAndUserRequests = taxonRequestBuilder.composeRequest(filter);
        List<NewStatisticsRow> rows = taxonRowCreator.createRows(filter, taxonAndUserRequests);
        return new NewStatisticsResult(filter, convertECRows(groupByEC(rows)), rowSummer.getSum(rows));
    }

    private Map<EducationalContext, List<NewStatisticsRow>> groupByEC(List<NewStatisticsRow> rows) {
        return rows.stream().collect(Collectors.groupingBy(NewStatisticsRow::getEducationalContext));
    }

    private List<EducationalContextRow> convertECRows(Map<EducationalContext, List<NewStatisticsRow>> collect) {
        return collect.entrySet().stream()
                        .map(entry -> new EducationalContextRow(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList());
    }


}
