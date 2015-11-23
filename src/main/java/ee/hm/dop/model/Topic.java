package ee.hm.dop.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("TOPIC")
public class Topic extends Taxon {

    @ManyToOne
    @JoinColumn(name = "subject")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "domain")
    private Domain domain;

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }
}
