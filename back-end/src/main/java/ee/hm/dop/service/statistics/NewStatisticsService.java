package ee.hm.dop.service.statistics;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.dao.UserDao;
import ee.hm.dop.dao.specialized.StatisticsDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.newdto.*;
import ee.hm.dop.utils.UserUtil;
import org.apache.commons.collections.CollectionUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

        if (CollectionUtils.isNotEmpty(filter.getUsers())) {
            //todo kus on educationalContextOrder?
            List<DomainWithChildren> domainsWithChildren = userRequestBuilder.userPath(filter);
            NewUserStatistics row = userRowCreator.createRows(filter, filter.getUsers().get(0), domainsWithChildren);
            List<NewUserStatistics> rows = Lists.newArrayList(row);
            Map<EducationalContext, NewStatisticsRow> collect = rows.stream().map(NewUserStatistics::getRows).flatMap(Collection::stream).collect(Collectors.toMap(NewStatisticsRow::getEducationalContext, f -> f));
            for (Map.Entry<EducationalContext, NewStatisticsRow> entry : collect.entrySet()) {
            }
            return new NewStatisticsResult(filter, collect, rowSummer.getSum(rows));
        }
        List<TaxonAndUserRequest> taxonAndUserRequests = taxonRequestBuilder.composeRequest(filter);
        NewUserStatistics row = taxonRowCreator.createRows(filter, taxonAndUserRequests);
        List<NewUserStatistics> rows = Lists.newArrayList(row);
        Map<EducationalContext, NewStatisticsRow> collect = rows.stream().map(NewUserStatistics::getRows).flatMap(Collection::stream).collect(Collectors.toMap(NewStatisticsRow::getEducationalContext, f -> f));
        return new NewStatisticsResult(filter, collect, rowSummer.getSum(rows));
        //iga taksoni juurde saada taxonId'de list
        //PM DomainWithChildrenAND Users
        //siis jätkub ülemine loogika


//        List<User> users = getUsers(filter);
//        if (CollectionUtils.isEmpty(users)) {
//            throw new IllegalArgumentException("no moderators");
//        }
//        List<TaxonWithChildren> taxons = getTaxons(filter);
//        List<UserStatistics> rows = createRows(filter, users, taxons);
//
//        return new StatisticsResult(filter, rows, getSum(rows));
        return null;
    }


}
