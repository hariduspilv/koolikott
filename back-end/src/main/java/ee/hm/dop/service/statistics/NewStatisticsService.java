package ee.hm.dop.service.statistics;

import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.dao.UserDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.newdto.*;
import ee.hm.dop.utils.UserUtil;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NewStatisticsService {

    @Inject
    private UserDao userDao;
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
    @Inject
    private TaxonDao taxonDao;

    public NewStatisticsResult statistics(StatisticsFilterDto filter, User loggedInUser) {
        UserUtil.mustBeAdmin(loggedInUser);

        if (filter.isUserSearch()) {
            return userSearchPath(filter);
        }
        return taxonSearchPath(filter);
    }

    private NewStatisticsResult userSearchPath(StatisticsFilterDto filter) {
        User dbUser = userDao.findById(filter.getUsers().get(0).getId());
        List<DomainWithChildren> domainsWithChildren = userRequestBuilder.userPath(dbUser);
        List<NewStatisticsRow> rows = userRowCreator.createRows(filter, dbUser, domainsWithChildren);
        return new NewStatisticsResult(filter, convertECRows(groupByEC(rows)), rowSummer.getSum(rows));
    }

    private NewStatisticsResult taxonSearchPath(StatisticsFilterDto filter) {
        List<Taxon> dbTaxons = refreshTaxons(filter);
        List<TaxonAndUserRequest> taxonAndUserRequests = taxonRequestBuilder.composeRequest(dbTaxons);
        List<NewStatisticsRow> rows = taxonRowCreator.createRows(filter, taxonAndUserRequests);
        return new NewStatisticsResult(filter, convertECRows(groupByEC(rows)), rowSummer.getSum(rows));
    }

    private List<Taxon> refreshTaxons(StatisticsFilterDto filter) {
        List<Long> taxonIds = filter.getTaxons().stream().map(Taxon::getId).collect(Collectors.toList());
        return taxonDao.findById(taxonIds);
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
