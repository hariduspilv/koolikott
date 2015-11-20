package ee.hm.dop.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("TOPIC")
public class Topic extends Taxon {

    @ManyToOne
    @JoinColumn(name = "subject", nullable = false)
    private Subject subject;

    protected Subject getSubject() {
        return subject;
    }

    protected void setSubject(Subject subject) {
        this.subject = subject;
    }

}
