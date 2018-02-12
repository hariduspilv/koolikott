package ee.hm.dop.service.reviewmanagement.newdto;

import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;

import java.util.List;

public class DomainWithChildren {

    private EducationalContext educationalContext;
    private Domain domain;
    /**
     * whether the domain object is used
     */
    private boolean domainIsUsed;
    /**
     * if domain then dont ask for children elements
     */
    private boolean capped;
    /**
     * domain + children ids
     */
    private List<Long> taxonIds;
    private List<SubjectWithChildren> subjects;

    public boolean isDomainIsUsed() {
        return domainIsUsed;
    }

    public void setDomainIsUsed(boolean domainIsUsed) {
        this.domainIsUsed = domainIsUsed;
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

    public boolean isCapped() {
        return capped;
    }

    public void setCapped(boolean capped) {
        this.capped = capped;
    }

    public List<Long> getTaxonIds() {
        return taxonIds;
    }

    public void setTaxonIds(List<Long> taxonIds) {
        this.taxonIds = taxonIds;
    }

    public List<SubjectWithChildren> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectWithChildren> subjects) {
        this.subjects = subjects;
    }
}
