package ee.hm.dop.service.statistics;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.specialized.StatisticsDao;
import ee.hm.dop.model.User;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsQuery;
import ee.hm.dop.service.reviewmanagement.newdto.DomainWithChildren;
import ee.hm.dop.service.reviewmanagement.newdto.NewStatisticsRow;
import ee.hm.dop.service.reviewmanagement.newdto.SubjectWithChildren;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NewStatisticsByUserRowCreator {

    private static final Logger logger = LoggerFactory.getLogger(NewStatisticsByUserRowCreator.class);
    @Inject
    private StatisticsDao statisticsDao;

    public List<NewStatisticsRow> createRows(StatisticsFilterDto filter, User user, List<DomainWithChildren> taxons) {
        List<NewStatisticsRow> rows = new ArrayList<>();
        for (DomainWithChildren domain : taxons) {
            NewStatisticsRow domainRow;
            if (domain.isDomainIsUsed()) {
                domainRow = convertFromUsedDomain(user, domain, filter.getFrom(), filter.getTo());
            } else {
                domainRow = convertFromEmptyDomain(user, domain);
            }
            for (SubjectWithChildren subject : domain.getSubjects()) {
                NewStatisticsRow subjectRow = convertFromSubject(user, subject, filter.getFrom(), filter.getTo());
                domainRow.getSubjects().add(subjectRow);
            }
            rows.add(domainRow);
        }
        return rows;
    }

    private NewStatisticsRow convertFromUsedDomain(User user, DomainWithChildren domain, DateTime from, DateTime to) {
        List<Long> taxonIds = domain.getTaxonIds();
        List<Long> userIds = Lists.newArrayList(user.getId());

        logger.info("convertFromSubject");
        logger.info("input userIds" + userIds);
        logger.info("input taxonIds" + taxonIds);
        logger.info("from: " + from.toString());
        logger.info("to: " + to.toString());
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

        NewStatisticsRow row = new NewStatisticsRow();
        row.setDomainUsed(true);
        row.setUser(user);
        row.setEducationalContext(domain.getEducationalContext());
        row.setDomain(domain.getDomain());
        row.setSubject(null);
        row.setReviewedLOCount(getCount(reviewedLOCount, user));
        row.setApprovedReportedLOCount(getCount(approvedReportedLOCount, user));
        row.setDeletedReportedLOCount(getCount(rejectedReportedLOCount, user));
        row.setAcceptedChangedLOCount(getCount(acceptedChangedLOCount, user));
        row.setRejectedChangedLOCount(getCount(rejectedChangedLOCount, user));
        row.setPortfolioCount(getCount(portfolioCount, user));
        row.setPublicPortfolioCount(getCount(publicPortfolioCount, user));
        row.setMaterialCount(getCount(materialCount, user));
        row.setSubjects(Lists.newArrayList());
        return row;
    }

    private NewStatisticsRow convertFromSubject(User user, SubjectWithChildren subject, DateTime from, DateTime to) {
        List<Long> userIds = Lists.newArrayList(user.getId());
        List<Long> taxonIds = subject.getTaxonIds();

        logger.info("convertFromSubject");
        logger.info("input userIds" + userIds);
        logger.info("input taxonIds" + taxonIds);
        logger.info("from: " + from.toString());
        logger.info("to: " + to.toString());
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

        NewStatisticsRow row = new NewStatisticsRow();
        row.setUser(user);
        row.setEducationalContext(subject.getEducationalContext());
        row.setDomain(subject.getDomain());
        row.setSubject(subject.getSubject());
        row.setReviewedLOCount(getCount(reviewedLOCount, user));
        row.setApprovedReportedLOCount(getCount(approvedReportedLOCount, user));
        row.setDeletedReportedLOCount(getCount(rejectedReportedLOCount, user));
        row.setAcceptedChangedLOCount(getCount(acceptedChangedLOCount, user));
        row.setRejectedChangedLOCount(getCount(rejectedChangedLOCount, user));
        row.setPortfolioCount(getCount(portfolioCount, user));
        row.setPublicPortfolioCount(getCount(publicPortfolioCount, user));
        row.setMaterialCount(getCount(materialCount, user));
        row.setSubjects(Lists.newArrayList());
        return row;
    }

    private Long getCount(List<StatisticsQuery> reviewed, User user) {
        Optional<StatisticsQuery> userQuery = reviewed.stream().filter(q -> q.getUserId().equals(user.getId())).findAny();
        return userQuery.map(StatisticsQuery::getCount).orElse(0L);
    }

    private NewStatisticsRow convertFromEmptyDomain(User user, DomainWithChildren domain) {
        NewStatisticsRow row = new NewStatisticsRow();
        row.setDomainUsed(false);
        row.setUser(user);
        row.setEducationalContext(domain.getEducationalContext());
        row.setDomain(domain.getDomain());
        row.setSubjects(Lists.newArrayList());
        return row;
    }
}
