package ee.hm.dop.service.statistics;

import ee.hm.dop.service.reviewmanagement.newdto.NewStatisticsRow;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class NewStatisticsRowSummer {

    public NewStatisticsRow getSum(List<NewStatisticsRow> userRows) {
        List<NewStatisticsRow> rowsToSum = new ArrayList<>();
        for (NewStatisticsRow domainRow : userRows) {
            if (domainRow.isDomainUsed()) {
                rowsToSum.add(domainRow);
            }
            if (CollectionUtils.isNotEmpty(domainRow.getSubjects())) {
                rowsToSum.addAll(domainRow.getSubjects());
            }
        }
        return rowsToSum.stream()
                .reduce(emptyRow(), (r1, r2) -> {
                    NewStatisticsRow sum = new NewStatisticsRow();
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

    private NewStatisticsRow emptyRow() {
        NewStatisticsRow identity = new NewStatisticsRow();
        identity.setUser(null);
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
}
