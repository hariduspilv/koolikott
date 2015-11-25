package ee.hm.dop.model.taxon;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DiscriminatorValue("SUBTOPIC")
public class Subtopic extends Taxon {

    @ManyToOne
    @JoinColumn(name = "topic", nullable = false)
    private Topic topic;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    @JsonIgnore
    @Override
    public Taxon getParent() {
        return getTopic();
    }
}
