package ee.hm.dop.service.statistics;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.specialized.StatisticsDao;
import ee.hm.dop.model.User;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsQuery;
import ee.hm.dop.service.reviewmanagement.newdto.DomainWithChildren;
import ee.hm.dop.service.reviewmanagement.newdto.NewStatisticsRow;
import ee.hm.dop.service.reviewmanagement.newdto.SubjectWithChildren;
import ee.hm.dop.service.reviewmanagement.newdto.TaxonAndUserRequest;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NewTaxonRowCreator {

    private static final Logger logger = LoggerFactory.getLogger(NewTaxonRowCreator.class);
    @Inject
    private StatisticsDao statisticsDao;

    public List<NewStatisticsRow> createRows(StatisticsFilterDto filter, List<TaxonAndUserRequest> taxonAndUserRequests) {
        List<NewStatisticsRow> resultRows = new ArrayList<>();
        logger.info("creating rows");
        for (TaxonAndUserRequest taxonAndUserRequest : taxonAndUserRequests) {
            List<NewStatisticsRow> domainRows;
            if (taxonAndUserRequest.isNoResults()) {
                domainRows = convertFromNoResults(taxonAndUserRequest);
            } else {
                if (taxonAndUserRequest.getDomainWithChildren().isDomainIsUsed()) {
                    domainRows = convertFromUsedDomain(taxonAndUserRequest.getUsers(), taxonAndUserRequest.getDomainWithChildren(), filter.getFrom(), filter.getTo());
                } else {
                    domainRows = convertFromEmptyDomain(taxonAndUserRequest.getUsers(), taxonAndUserRequest.getDomainWithChildren(), filter.getFrom(), filter.getTo());
                }
                for (SubjectWithChildren subject : taxonAndUserRequest.getDomainWithChildren().getSubjects()) {
                    List<NewStatisticsRow> subjectRows = convertFromSubject(taxonAndUserRequest.getUsers(), subject, filter.getFrom(), filter.getTo());
                    domainRows.get(domainRows.size() - 1).getSubjects().addAll(subjectRows);
                }

            }
            resultRows.addAll(domainRows);
        }
        return resultRows;
    }

    private List<NewStatisticsRow> convertFromNoResults(TaxonAndUserRequest taxonAndUserRequest) {
        NewStatisticsRow row = new NewStatisticsRow();
        row.setNoUsersFound(true);
        row.setEducationalContext(taxonAndUserRequest.getEducationalContext());
        row.setDomain(taxonAndUserRequest.getDomain());
        row.setSubject(taxonAndUserRequest.getSubject());
        row.setSubjects(Lists.newArrayList());
        return Lists.newArrayList(row);
    }

    private List<NewStatisticsRow> convertFromUsedDomain(List<User> users, DomainWithChildren domain, DateTime from, DateTime to) {
        List<Long> taxonIds = domain.getTaxonIds();
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        logger.info("convertFromUsedDomain");
        logger.info("input userIds" + userIds);
        logger.info("input taxonIds" + taxonIds);
        if (from != null) {
            logger.info("from: " + from.toString());
        }
        if (to != null) {
            logger.info("to: " + to.toString());
        }
        List<StatisticsQuery> reviewedLOCount = statisticsDao.reviewedLOCount(from, to, userIds, taxonIds);
        List<StatisticsQuery> approvedReportedLOCount = statisticsDao.approvedReportedLOCount(from, to, userIds, taxonIds);
        List<StatisticsQuery> rejectedReportedLOCount = statisticsDao.rejectedReportedLOCount(from, to, userIds, taxonIds);
        List<StatisticsQuery> acceptedChangedLOCount = statisticsDao.acceptedChangedLOCount(from, to, userIds, taxonIds);
        List<StatisticsQuery> rejectedChangedLOCount = statisticsDao.rejectedChangedLOCount(from, to, userIds, taxonIds);
        List<StatisticsQuery> portfolioCount = statisticsDao.createdPortfolioCount(from, to, userIds, taxonIds);
        List<StatisticsQuery> publicPortfolioCount = statisticsDao.createdPublicPortfolioCount(from, to, userIds, taxonIds);
        List<StatisticsQuery> materialCount = statisticsDao.createdMaterialCount(from, to, userIds, taxonIds);
        if (reviewedLOCount.isEmpty()) {
            logger.info("output is empty");
        } else {
            for (StatisticsQuery query : reviewedLOCount) {
                logger.info("query result uId " + query.getUserId() + "c " + query.getCount());
            }
        }

        List<NewStatisticsRow> rows = new ArrayList<>();
        for (User user : users) {
            NewStatisticsRow row = new NewStatisticsRow();
            row.setDomainUsed(true);
            row.setUser(user);
            row.setEducationalContext(domain.getEducationalContext());
            row.setDomain(domain.getDomain());
            row.setSubject(null);
            Optional<StatisticsQuery> userQuery = reviewedLOCount.stream().filter(q -> q.getUserId().equals(user.getId())).findAny();
            if (userQuery.isPresent()) {
                logger.info("user found");
            } else {
                logger.info("user not found");
            }
            Long count = userQuery.map(StatisticsQuery::getCount).orElse(0L);
            logger.info("count" + count);
            row.setReviewedLOCount(count);
            row.setApprovedReportedLOCount(getCount(approvedReportedLOCount, user));
            row.setDeletedReportedLOCount(getCount(rejectedReportedLOCount, user));
            row.setAcceptedChangedLOCount(getCount(acceptedChangedLOCount, user));
            row.setRejectedChangedLOCount(getCount(rejectedChangedLOCount, user));
            row.setPortfolioCount(getCount(portfolioCount, user));
            row.setPublicPortfolioCount(getCount(publicPortfolioCount, user));
            row.setMaterialCount(getCount(materialCount, user));
            row.setSubjects(Lists.newArrayList());
            rows.add(row);
        }
        return rows;
    }

    private List<NewStatisticsRow> convertFromSubject(List<User> users, SubjectWithChildren subject, DateTime from, DateTime to) {
        List<Long> taxonIds = subject.getTaxonIds();
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());

        logger.info("convertFromSubject");
        logger.info("input userIds" + userIds);
        logger.info("input taxonIds" + taxonIds);
        if (from != null) {
            logger.info("from: " + from.toString());
        }
        if (to != null) {
            logger.info("to: " + to.toString());
        }
        List<StatisticsQuery> reviewedLOCount = statisticsDao.reviewedLOCount(from, to, userIds, taxonIds);
        List<StatisticsQuery> approvedReportedLOCount = statisticsDao.approvedReportedLOCount(from, to, userIds, taxonIds);
        List<StatisticsQuery> rejectedReportedLOCount = statisticsDao.rejectedReportedLOCount(from, to, userIds, taxonIds);
        List<StatisticsQuery> acceptedChangedLOCount = statisticsDao.acceptedChangedLOCount(from, to, userIds, taxonIds);
        List<StatisticsQuery> rejectedChangedLOCount = statisticsDao.rejectedChangedLOCount(from, to, userIds, taxonIds);
        List<StatisticsQuery> portfolioCount = statisticsDao.createdPortfolioCount(from, to, userIds, taxonIds);
        List<StatisticsQuery> publicPortfolioCount = statisticsDao.createdPublicPortfolioCount(from, to, userIds, taxonIds);
        List<StatisticsQuery> materialCount = statisticsDao.createdMaterialCount(from, to, userIds, taxonIds);
        if (reviewedLOCount.isEmpty()) {
            logger.info("output is empty");
        } else {
            for (StatisticsQuery query : reviewedLOCount) {
                logger.info("query result uId " + query.getUserId() + "c " + query.getCount());
            }
        }

        List<NewStatisticsRow> rows = new ArrayList<>();
        for (User user : users) {
            NewStatisticsRow row = new NewStatisticsRow();
            row.setUser(user);
            row.setEducationalContext(subject.getEducationalContext());
            row.setDomain(subject.getDomain());
            row.setSubject(subject.getSubject());
            Optional<StatisticsQuery> userQuery = reviewedLOCount.stream().filter(q -> q.getUserId().equals(user.getId())).findAny();
            if (userQuery.isPresent()) {
                logger.info("user found");
            } else {
                logger.info("user not found");
            }
            Long count = userQuery.map(StatisticsQuery::getCount).orElse(0L);
            logger.info("count" + count);
            row.setReviewedLOCount(count);
            row.setApprovedReportedLOCount(getCount(approvedReportedLOCount, user));
            row.setDeletedReportedLOCount(getCount(rejectedReportedLOCount, user));
            row.setAcceptedChangedLOCount(getCount(acceptedChangedLOCount, user));
            row.setRejectedChangedLOCount(getCount(rejectedChangedLOCount, user));
            row.setPortfolioCount(getCount(portfolioCount, user));
            row.setPublicPortfolioCount(getCount(publicPortfolioCount, user));
            row.setMaterialCount(getCount(materialCount, user));
            rows.add(row);
        }
        return rows;
    }

    private List<NewStatisticsRow> convertFromEmptyDomain(List<User> users, DomainWithChildren domain, DateTime from, DateTime to) {
        NewStatisticsRow row = new NewStatisticsRow();
        row.setDomainUsed(false);
        row.setEducationalContext(domain.getEducationalContext());
        row.setDomain(domain.getDomain());
        row.setSubjects(Lists.newArrayList());
        return Lists.newArrayList(row);
    }

    private Long getCount(List<StatisticsQuery> reviewed, User user) {
        Optional<StatisticsQuery> userQuery = reviewed.stream().filter(q -> q.getUserId().equals(user.getId())).findAny();
        return userQuery.map(StatisticsQuery::getCount).orElse(0L);
    }
}
