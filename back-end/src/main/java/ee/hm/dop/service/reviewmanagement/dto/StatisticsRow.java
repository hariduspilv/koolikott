package ee.hm.dop.service.reviewmanagement.dto;

import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Taxon;

public class StatisticsRow {

    private User user;
    private Taxon taxon;
    private Long reviewedLOCount;
    private Long approvedReportedLOCount;
    private Long deletedReportedLOCount;
    private Long acceptedChangedLOCount;
    private Long rejectedChangedLOCount;
    private Long reportedLOCount;
    private Long portfolioCount;
    private Long publicPortfolioCount;
    private Long materialCount;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Taxon getTaxon() {
        return taxon;
    }

    public void setTaxon(Taxon taxon) {
        this.taxon = taxon;
    }

    public Long getReviewedLOCount() {
        return reviewedLOCount;
    }

    public void setReviewedLOCount(Long reviewedLOCount) {
        this.reviewedLOCount = reviewedLOCount;
    }

    public Long getApprovedReportedLOCount() {
        return approvedReportedLOCount;
    }

    public void setApprovedReportedLOCount(Long approvedReportedLOCount) {
        this.approvedReportedLOCount = approvedReportedLOCount;
    }

    public Long getDeletedReportedLOCount() {
        return deletedReportedLOCount;
    }

    public void setDeletedReportedLOCount(Long deletedReportedLOCount) {
        this.deletedReportedLOCount = deletedReportedLOCount;
    }

    public Long getAcceptedChangedLOCount() {
        return acceptedChangedLOCount;
    }

    public void setAcceptedChangedLOCount(Long acceptedChangedLOCount) {
        this.acceptedChangedLOCount = acceptedChangedLOCount;
    }

    public Long getRejectedChangedLOCount() {
        return rejectedChangedLOCount;
    }

    public void setRejectedChangedLOCount(Long rejectedChangedLOCount) {
        this.rejectedChangedLOCount = rejectedChangedLOCount;
    }

    public Long getReportedLOCount() {
        return reportedLOCount;
    }

    public void setReportedLOCount(Long reportedLOCount) {
        this.reportedLOCount = reportedLOCount;
    }

    public Long getPortfolioCount() {
        return portfolioCount;
    }

    public void setPortfolioCount(Long portfolioCount) {
        this.portfolioCount = portfolioCount;
    }

    public Long getPublicPortfolioCount() {
        return publicPortfolioCount;
    }

    public void setPublicPortfolioCount(Long publicPortfolioCount) {
        this.publicPortfolioCount = publicPortfolioCount;
    }

    public Long getMaterialCount() {
        return materialCount;
    }

    public void setMaterialCount(Long materialCount) {
        this.materialCount = materialCount;
    }
}
