package ee.hm.dop.service.reviewmanagement.newdto;

import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.Subject;

import java.util.Objects;

public class TranslationTaxon {

    private String translation;
    private Domain domain;
    private Subject subject;

    public TranslationTaxon(String translation, Domain domain) {
        this.translation = translation;
        this.domain = domain;
    }

    public TranslationTaxon(String translation, Domain domain, Subject subject) {
        this.translation = translation;
        this.domain = domain;
        this.subject = subject;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslationTaxon that = (TranslationTaxon) o;
        return Objects.equals(translation, that.translation) &&
                Objects.equals(domain, that.domain) &&
                Objects.equals(subject, that.subject);
    }

    @Override
    public int hashCode() {

        return Objects.hash(translation, domain, subject);
    }
}
