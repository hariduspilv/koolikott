package ee.hm.dop.model.taxon;

import static javax.persistence.FetchType.LAZY;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

import java.util.Set;

@Entity
@DiscriminatorValue("SUBTOPIC")
public class Subtopic extends Taxon {

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "topic", nullable = false)
    @Where(clause = "used = 1")
    private Topic topic;

    @JsonIgnore
    @Column(nullable = false, insertable = false)
    private boolean used;

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

    @JsonIgnore
    @Override
    public String getSolrLevel() {
        return "subtopic";
    }

    @JsonIgnore
    @Override
    public Set<? extends Taxon> getChildren() {
        return null;
    }

    @Override
    public boolean isUsed() {
        return used;
    }

    @Override
    public void setUsed(boolean used) {
        this.used = used;
    }
}
