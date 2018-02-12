package ee.hm.dop.service.reviewmanagement.newdto;

import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Taxon;

import java.util.List;

public class NewStatisticsRow {

    private User user;
    private EducationalContext educationalContext;
    private Domain domain;
    /**
     * domain row does not contain subject
     */
    private Subject subject;
    private boolean domainUsed;
    private Taxon usertaxon;
    private Long reviewedLOCount;
    private Long approvedReportedLOCount;
    private Long deletedReportedLOCount;
    private Long acceptedChangedLOCount;
    private Long rejectedChangedLOCount;
    private Long reportedLOCount;
    private Long portfolioCount;
    private Long publicPortfolioCount;
    private Long materialCount;

    public boolean isDomainUsed() {
        return domainUsed;
    }

    public void setDomainUsed(boolean domainUsed) {
        this.domainUsed = domainUsed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EducationalContext getEducationalContext() {
        return educationalContext;
    }

    public void setEducationalContext(EducationalContext educationalContext) {
        this.educationalContext = educationalContext;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Taxon getUsertaxon() {
        return usertaxon;
    }

    public void setUsertaxon(Taxon usertaxon) {
        this.usertaxon = usertaxon;
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
