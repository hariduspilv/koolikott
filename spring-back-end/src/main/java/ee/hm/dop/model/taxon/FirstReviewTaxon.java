package ee.hm.dop.model.taxon;


import java.util.Objects;

public class FirstReviewTaxon {

    private TaxonDTO domain;
    private TaxonDTO subject;
    private TaxonDTO educationalContext;

    public FirstReviewTaxon() {
    }

    public FirstReviewTaxon(TaxonDTO educationalContext, TaxonDTO domain, TaxonDTO subject)  {
        this.domain = domain;
        this.subject = subject;
        this.educationalContext = educationalContext;
    }

    public TaxonDTO getSubject() {
        return subject;
    }

    public void setSubject(TaxonDTO subject) {
        this.subject = subject;
    }

    public TaxonDTO getDomain() {
        return domain;
    }

    public void setDomain(TaxonDTO domain) {
        this.domain = domain;
    }

    public TaxonDTO getEducationalContext() {
        return educationalContext;
    }

    public void setEducationalContext(TaxonDTO educationalContext) {
        this.educationalContext = educationalContext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirstReviewTaxon that = (FirstReviewTaxon) o;
        return Objects.equals(domain, that.domain) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(educationalContext, that.educationalContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, subject, educationalContext);
    }
}
