package ee.hm.dop.service.statistics;

import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.dao.UserDao;
import ee.hm.dop.dao.specialized.StatisticsDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsQuery;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsResult;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsRow;
import ee.hm.dop.utils.UserUtil;
import org.apache.commons.collections.CollectionUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StatisticsService {

    @Inject
    private UserDao userDao;
    @Inject
    private TaxonDao taxonDao;
    @Inject
    private StatisticsDao statisticsDao;

    public StatisticsResult statistics(StatisticsFilterDto filter, User loggedInUser) {
        UserUtil.mustBeAdmin(loggedInUser);

        List<User> users = getUsers(filter);
        if (CollectionUtils.isEmpty(users)){
            throw new IllegalArgumentException("no moderators");
        }
        List<Long> taxons = getTaxons(filter);

        List<StatisticsRow> rows = createRows(filter, users, taxons);

        return new StatisticsResult(filter, rows, getSum(rows));
    }

    private List<StatisticsRow> createRows(StatisticsFilterDto filter, List<User> users, List<Long> taxons) {
        List<StatisticsQuery> reviewedLOCount = statisticsDao.reviewedLOCount(filter.getFrom(), filter.getTo(), users, taxons);
        List<StatisticsQuery> approvedReportedLOCount = statisticsDao.approvedReportedLOCount(filter.getFrom(), filter.getTo(), users, taxons);
        List<StatisticsQuery> rejectedReportedLOCount = statisticsDao.rejectedReportedLOCount(filter.getFrom(), filter.getTo(), users, taxons);
        List<StatisticsQuery> acceptedChangedLOCount = statisticsDao.acceptedChangedLOCount(filter.getFrom(), filter.getTo(), users, taxons);
        List<StatisticsQuery> rejectedChangedLOCount = statisticsDao.rejectedChangedLOCount(filter.getFrom(), filter.getTo(), users, taxons);
        List<StatisticsQuery> reportedLOCount = statisticsDao.reportedLOCount(filter.getFrom(), filter.getTo(), users, taxons);
        List<StatisticsQuery> portfolioCount = statisticsDao.createdPortfolioCount(filter.getFrom(), filter.getTo(), users, taxons);
        List<StatisticsQuery> publicPortfolioCount = statisticsDao.createdPublicPortfolioCount(filter.getFrom(), filter.getTo(), users, taxons);
        List<StatisticsQuery> materialCount = statisticsDao.createdMaterialCount(filter.getFrom(), filter.getTo(), users, taxons);
        List<StatisticsRow> rows = new ArrayList<>();
        for (User user : users) {
            StatisticsRow row = new StatisticsRow();
            row.setUser(user);
            row.setUsertaxons(taxonDao.getUserTaxons(user));
            row.setReviewedLOCount(getCount(reviewedLOCount, user));
            row.setApprovedReportedLOCount(getCount(approvedReportedLOCount, user));
            row.setDeletedReportedLOCount(getCount(rejectedReportedLOCount, user));
            row.setAcceptedChangedLOCount(getCount(acceptedChangedLOCount, user));
            row.setRejectedChangedLOCount(getCount(rejectedChangedLOCount, user));
            row.setReportedLOCount(getCount(reportedLOCount, user));
            row.setPortfolioCount(getCount(portfolioCount, user));
            row.setPublicPortfolioCount(getCount(publicPortfolioCount, user));
            row.setMaterialCount(getCount(materialCount, user));
            rows.add(row);
        }
        return rows;
    }

    private StatisticsRow getSum(List<StatisticsRow> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return null;
        }
        return rows.stream().reduce(emptyRow(),  (r1, r2) -> {
            StatisticsRow sum = new StatisticsRow();
            sum.setReviewedLOCount(r1.getReviewedLOCount() + r2.getReviewedLOCount());
            sum.setApprovedReportedLOCount(r1.getApprovedReportedLOCount() + r2.getApprovedReportedLOCount());
            sum.setDeletedReportedLOCount(r1.getDeletedReportedLOCount() + r2.getDeletedReportedLOCount());
            sum.setAcceptedChangedLOCount(r1.getAcceptedChangedLOCount() + r2.getAcceptedChangedLOCount());
            sum.setRejectedChangedLOCount(r1.getRejectedChangedLOCount() + r2.getRejectedChangedLOCount());
            sum.setReportedLOCount(r1.getReportedLOCount() + r2.getReportedLOCount());
            sum.setPortfolioCount(r1.getPortfolioCount() + r2.getPortfolioCount());
            sum.setPublicPortfolioCount(r1.getPublicPortfolioCount() + r2.getPublicPortfolioCount());
            sum.setMaterialCount(r1.getMaterialCount() + r2.getMaterialCount());
            return sum;
        });
    }

    private StatisticsRow emptyRow() {
        StatisticsRow identity = new StatisticsRow();
        identity.setUser(null);
        identity.setUsertaxons(null);
        identity.setReviewedLOCount(0L);
        identity.setApprovedReportedLOCount(0L);
        identity.setDeletedReportedLOCount(0L);
        identity.setAcceptedChangedLOCount(0L);
        identity.setRejectedChangedLOCount(0L);
        identity.setReportedLOCount(0L);
        identity.setPortfolioCount(0L);
        identity.setPublicPortfolioCount(0L);
        identity.setMaterialCount(0L);
        return identity;
    }

    private Long getCount(List<StatisticsQuery> reviewed, User user) {
        Optional<StatisticsQuery> userQuery = reviewed.stream().filter(q -> q.getUserId().equals(user.getId())).findAny();
        return userQuery.map(StatisticsQuery::getCount).orElse(0L);
    }

    private List<User> getUsers(StatisticsFilterDto filter) {
        if (filter.getUser() != null) {
            return Arrays.asList(userDao.findById(filter.getUser().getId()));
        } else {
            return userDao.getUsersByRole(Role.MODERATOR);
        }
    }

    private List<Long> getTaxons(StatisticsFilterDto filter) {
        if (filter.getTaxon() != null) {
            return taxonDao.getTaxonWithChildren(filter.getTaxon());
        } else {
            return new ArrayList<>();
        }
    }
}
