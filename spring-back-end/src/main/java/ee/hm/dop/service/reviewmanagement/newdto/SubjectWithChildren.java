package ee.hm.dop.service.reviewmanagement.newdto;

import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Subject;

import java.util.List;

public class SubjectWithChildren {

    private EducationalContext educationalContext;
    private Domain domain;
    private Subject subject;
    /**
     * subject + children ids
     */
    private List<Long> taxonIds;

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

    public List<Long> getTaxonIds() {
        return taxonIds;
    }

    public void setTaxonIds(List<Long> taxonIds) {
        this.taxonIds = taxonIds;
    }
}
