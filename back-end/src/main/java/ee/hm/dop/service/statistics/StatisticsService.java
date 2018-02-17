package ee.hm.dop.service.statistics;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.TaxonDao;
import ee.hm.dop.dao.UserDao;
import ee.hm.dop.dao.specialized.StatisticsDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.service.reviewmanagement.dto.*;
import ee.hm.dop.utils.UserUtil;
import org.apache.commons.collections.CollectionUtils;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

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
        if (CollectionUtils.isEmpty(users)) {
            throw new IllegalArgumentException("no moderators");
        }
        List<TaxonWithChildren> taxons = getTaxons(filter);
        List<UserStatistics> rows = createRows(filter, users, taxons);

        return new StatisticsResult(filter, rows, getSum(rows));
    }

    private List<UserStatistics> createRows(StatisticsFilterDto filter, List<User> users, List<TaxonWithChildren> taxons) {
        List<UserStatistics> statistics = makeEmptyUserStatistics(users);
        for (TaxonWithChildren taxon : taxons) {
            List<StatisticsQuery> reviewedLOCount = statisticsDao.reviewedLOCount(filter.getFrom(), filter.getTo(), users, taxon.getTaxonIds());
            List<StatisticsQuery> approvedReportedLOCount = statisticsDao.approvedReportedLOCount(filter.getFrom(), filter.getTo(), users, taxon.getTaxonIds());
            List<StatisticsQuery> rejectedReportedLOCount = statisticsDao.rejectedReportedLOCount(filter.getFrom(), filter.getTo(), users, taxon.getTaxonIds());
            List<StatisticsQuery> acceptedChangedLOCount = statisticsDao.acceptedChangedLOCount(filter.getFrom(), filter.getTo(), users, taxon.getTaxonIds());
            List<StatisticsQuery> rejectedChangedLOCount = statisticsDao.rejectedChangedLOCount(filter.getFrom(), filter.getTo(), users, taxon.getTaxonIds());
            List<StatisticsQuery> portfolioCount = statisticsDao.createdPortfolioCount(filter.getFrom(), filter.getTo(), users, taxon.getTaxonIds());
            List<StatisticsQuery> publicPortfolioCount = statisticsDao.createdPublicPortfolioCount(filter.getFrom(), filter.getTo(), users, taxon.getTaxonIds());
            List<StatisticsQuery> materialCount = statisticsDao.createdMaterialCount(filter.getFrom(), filter.getTo(), users, taxon.getTaxonIds());
            for (User user : users) {
                UserStatistics statistic = getStatistic(statistics, user);
                if (statistic.getUserTaxons().stream().map(Taxon::getId).anyMatch(t -> taxon.getTaxon().getId().equals(t))) {
                    StatisticsRow row = new StatisticsRow();
                    row.setUser(user);
                    row.setUsertaxon(taxon.getTaxon());
                    row.setReviewedLOCount(getCount(reviewedLOCount, user));
                    row.setApprovedReportedLOCount(getCount(approvedReportedLOCount, user));
                    row.setDeletedReportedLOCount(getCount(rejectedReportedLOCount, user));
                    row.setAcceptedChangedLOCount(getCount(acceptedChangedLOCount, user));
                    row.setRejectedChangedLOCount(getCount(rejectedChangedLOCount, user));
                    row.setPortfolioCount(getCount(portfolioCount, user));
                    row.setPublicPortfolioCount(getCount(publicPortfolioCount, user));
                    row.setMaterialCount(getCount(materialCount, user));
                    row.setSubjects(new ArrayList<>());
                    statistic.getRows().add(row);
                }
            }
            if (CollectionUtils.isNotEmpty(taxon.getSubjects())) {
                for (TaxonWithChildren child : taxon.getSubjects()) {
                    List<StatisticsQuery> reviewedLOCount2 = statisticsDao.reviewedLOCount(filter.getFrom(), filter.getTo(), users, child.getTaxonIds());
                    List<StatisticsQuery> approvedReportedLOCount2 = statisticsDao.approvedReportedLOCount(filter.getFrom(), filter.getTo(), users, child.getTaxonIds());
                    List<StatisticsQuery> rejectedReportedLOCount2 = statisticsDao.rejectedReportedLOCount(filter.getFrom(), filter.getTo(), users, child.getTaxonIds());
                    List<StatisticsQuery> acceptedChangedLOCount2 = statisticsDao.acceptedChangedLOCount(filter.getFrom(), filter.getTo(), users, child.getTaxonIds());
                    List<StatisticsQuery> rejectedChangedLOCount2 = statisticsDao.rejectedChangedLOCount(filter.getFrom(), filter.getTo(), users, child.getTaxonIds());
                    List<StatisticsQuery> portfolioCount2 = statisticsDao.createdPortfolioCount(filter.getFrom(), filter.getTo(), users, child.getTaxonIds());
                    List<StatisticsQuery> publicPortfolioCount2 = statisticsDao.createdPublicPortfolioCount(filter.getFrom(), filter.getTo(), users, child.getTaxonIds());
                    List<StatisticsQuery> materialCount2 = statisticsDao.createdMaterialCount(filter.getFrom(), filter.getTo(), users, child.getTaxonIds());
                    for (User user : users) {
                        UserStatistics statistic = getStatistic(statistics, user);
                        if (statistic.getUserTaxons().stream().map(Taxon::getId).anyMatch(t -> taxon.getTaxon().getId().equals(t))) {
                            Optional<StatisticsRow> parentRow = statistic.getRows().stream().filter(r -> r.getUsertaxon().getId().equals(child.getParent().getId())).findAny();
                            if (parentRow.isPresent()) {
                                StatisticsRow row = new StatisticsRow();
                                row.setUser(user);
                                row.setUsertaxon(taxon.getTaxon());
                                row.setReviewedLOCount(getCount(reviewedLOCount2, user));
                                row.setApprovedReportedLOCount(getCount(approvedReportedLOCount2, user));
                                row.setDeletedReportedLOCount(getCount(rejectedReportedLOCount2, user));
                                row.setAcceptedChangedLOCount(getCount(acceptedChangedLOCount2, user));
                                row.setRejectedChangedLOCount(getCount(rejectedChangedLOCount2, user));
                                row.setPortfolioCount(getCount(portfolioCount2, user));
                                row.setPublicPortfolioCount(getCount(publicPortfolioCount2, user));
                                row.setMaterialCount(getCount(materialCount2, user));
                                parentRow.get().getSubjects().add(row);
                            } else {
                                StatisticsRow row = new StatisticsRow();
                                row.setUser(user);
                                row.setUsertaxon(taxon.getTaxon());
                                row.setReviewedLOCount(getCount(reviewedLOCount2, user));
                                row.setApprovedReportedLOCount(getCount(approvedReportedLOCount2, user));
                                row.setDeletedReportedLOCount(getCount(rejectedReportedLOCount2, user));
                                row.setAcceptedChangedLOCount(getCount(acceptedChangedLOCount2, user));
                                row.setRejectedChangedLOCount(getCount(rejectedChangedLOCount2, user));
                                row.setPortfolioCount(getCount(portfolioCount2, user));
                                row.setPublicPortfolioCount(getCount(publicPortfolioCount2, user));
                                row.setMaterialCount(getCount(materialCount2, user));
                                getStatistic(statistics, user).getRows().add(row);
                            }
                        }
                    }
                }
            }
        }
        return statistics;
    }

    private List<UserStatistics> makeEmptyUserStatistics(List<User> users) {
        return users.stream().map(this::convert).collect(Collectors.toList());
    }

    private UserStatistics convert(User user) {
        UserStatistics userStatistics = new UserStatistics();
        userStatistics.setUser(user);
        userStatistics.setUserTaxons(taxonDao.getUserTaxons(user));
        userStatistics.setRows(new ArrayList<>());
        return userStatistics;
    }

    private UserStatistics getStatistic(List<UserStatistics> statistics, User user) {
        return statistics.stream().filter(s -> s.getUser().equals(user)).findAny().orElseThrow(RuntimeException::new);
    }

    private StatisticsRow getSum(List<UserStatistics> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return null;
        }
        List<StatisticsRow> results = new ArrayList<>();
        for (UserStatistics row : rows) {
            results.addAll(row.getRows());
            for (StatisticsRow innerRow : row.getRows()) {
                if (CollectionUtils.isNotEmpty(innerRow.getSubjects())){
                    results.addAll(innerRow.getSubjects());
                }
            }
        }
        return results.stream()
                .reduce(emptyRow(), (r1, r2) -> {
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
        identity.setUsertaxon(null);
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
        if (isNotEmpty(filter.getUsers())) {
            List<Long> userIds = filter.getUsers().stream().map(User::getId).collect(Collectors.toList());
            return userDao.findById(userIds);
        }
        return userDao.getUsersByRole(Role.MODERATOR);
    }

    private List<TaxonWithChildren> getTaxons(StatisticsFilterDto filter) {
        if (isEmpty(filter.getTaxons())) {
            filter.setTaxons(taxonDao.findTaxonByLevel(TaxonDao.EDUCATIONAL_CONTEXT));
        }
        List<TaxonWithChildren> taxonWithChildren = new ArrayList<>();
        for (Taxon taxon : filter.getTaxons()) {
            if (taxon instanceof EducationalContext) {
                EducationalContext educationalContext = (EducationalContext) taxon;
                for (Domain domain : educationalContext.getDomains()) {
                    taxonWithChildren.add(convertDomain(domain));
                }
            }
            if (taxon instanceof Domain) {
                Domain domain = (Domain) taxon;
                taxonWithChildren.add(convertDomain(domain));
            }
            if (taxon instanceof Subject) {
                TaxonWithChildren parent = new TaxonWithChildren();
                parent.setTaxon(taxon);
                taxonWithChildren.add(parent);
            }
        }
        return getTaxonsWithChildren(taxonWithChildren);
    }

    private TaxonWithChildren convertDomain(Domain domain) {
        TaxonWithChildren parent = new TaxonWithChildren();
        parent.setTaxon(domain);
        parent.setSubjects(new ArrayList<>());
        if (CollectionUtils.isEmpty(domain.getSubjects())) {
            parent.setCapped(true);
        } else {
            for (Subject subject : domain.getSubjects()) {
                TaxonWithChildren children = new TaxonWithChildren();
                children.setTaxon(subject);
                children.setParent(domain);
                parent.getSubjects().add(children);
            }
        }
        return parent;
    }

    public List<TaxonWithChildren> getTaxonsWithChildren(List<TaxonWithChildren> taxons) {
        for (TaxonWithChildren taxon : taxons) {
            if (taxon.isCapped()) {
                taxon.setTaxonIds(Lists.newArrayList(taxon.getTaxon().getId()));
            } else {
                taxon.setTaxonIds(taxonDao.getTaxonWithChildren(taxon.getTaxon()));
            }
            if (CollectionUtils.isNotEmpty(taxon.getSubjects())) {
                for (TaxonWithChildren child : taxon.getSubjects()) {
                    if (child.isCapped()) {
                        child.setTaxonIds(Lists.newArrayList(child.getTaxon().getId()));
                    } else {
                        child.setTaxonIds(taxonDao.getTaxonWithChildren(child.getTaxon()));
                    }
                }
            }
        }
        return taxons;
    }
}
