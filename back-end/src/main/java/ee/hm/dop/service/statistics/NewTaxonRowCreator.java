package ee.hm.dop.service.statistics;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.specialized.StatisticsDao;
import ee.hm.dop.model.User;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsQuery;
import ee.hm.dop.service.reviewmanagement.newdto.*;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NewTaxonRowCreator {

    @Inject
    private StatisticsDao statisticsDao;

    public List<NewStatisticsRow> createRows(StatisticsFilterDto filter, List<TaxonAndUserRequest> taxonAndUserRequests) {
        List<NewStatisticsRow> rows = new ArrayList<>();
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
                    domainRows.get(domainRows.size()-1).getSubjects().addAll(subjectRows);
                }

            }
            rows.addAll(domainRows);
        }
        return rows;
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
        List<StatisticsQuery> reviewedLOCount = statisticsDao.reviewedLOCount(from, to, users, taxonIds);
        List<StatisticsQuery> approvedReportedLOCount = statisticsDao.approvedReportedLOCount(from, to, users, taxonIds);
        List<StatisticsQuery> rejectedReportedLOCount = statisticsDao.rejectedReportedLOCount(from, to, users, taxonIds);
        List<StatisticsQuery> acceptedChangedLOCount = statisticsDao.acceptedChangedLOCount(from, to, users, taxonIds);
        List<StatisticsQuery> rejectedChangedLOCount = statisticsDao.rejectedChangedLOCount(from, to, users, taxonIds);
        List<StatisticsQuery> reportedLOCount = statisticsDao.reportedLOCount(from, to, users, taxonIds);
        List<StatisticsQuery> portfolioCount = statisticsDao.createdPortfolioCount(from, to, users, taxonIds);
        List<StatisticsQuery> publicPortfolioCount = statisticsDao.createdPublicPortfolioCount(from, to, users, taxonIds);
        List<StatisticsQuery> materialCount = statisticsDao.createdMaterialCount(from, to, users, taxonIds);

        List<NewStatisticsRow> rows = new ArrayList<>();
        for (User user : users) {
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
            row.setReportedLOCount(getCount(reportedLOCount, user));
            row.setPortfolioCount(getCount(portfolioCount, user));
            row.setPublicPortfolioCount(getCount(publicPortfolioCount, user));
            row.setMaterialCount(getCount(materialCount, user));
            rows.add(row);
        }
        return rows;
    }

    private List<NewStatisticsRow> convertFromSubject(List<User> users, SubjectWithChildren subject, DateTime from, DateTime to) {
        List<Long> taxonIds = subject.getTaxonIds();
        List<StatisticsQuery> reviewedLOCount = statisticsDao.reviewedLOCount(from, to, users, taxonIds);
        List<StatisticsQuery> approvedReportedLOCount = statisticsDao.approvedReportedLOCount(from, to, users, taxonIds);
        List<StatisticsQuery> rejectedReportedLOCount = statisticsDao.rejectedReportedLOCount(from, to, users, taxonIds);
        List<StatisticsQuery> acceptedChangedLOCount = statisticsDao.acceptedChangedLOCount(from, to, users, taxonIds);
        List<StatisticsQuery> rejectedChangedLOCount = statisticsDao.rejectedChangedLOCount(from, to, users, taxonIds);
        List<StatisticsQuery> reportedLOCount = statisticsDao.reportedLOCount(from, to, users, taxonIds);
        List<StatisticsQuery> portfolioCount = statisticsDao.createdPortfolioCount(from, to, users, taxonIds);
        List<StatisticsQuery> publicPortfolioCount = statisticsDao.createdPublicPortfolioCount(from, to, users, taxonIds);
        List<StatisticsQuery> materialCount = statisticsDao.createdMaterialCount(from, to, users, taxonIds);

        List<NewStatisticsRow> rows = new ArrayList<>();
        for (User user : users) {
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
            row.setReportedLOCount(getCount(reportedLOCount, user));
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
