package ee.hm.dop.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ChapterObject extends LearningObject implements Searchable {

    @Column(columnDefinition = "TEXT")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
