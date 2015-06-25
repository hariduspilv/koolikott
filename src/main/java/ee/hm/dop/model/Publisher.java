package ee.hm.dop.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by mart.laus on 22.06.2015.
 */
@Entity
public class Publisher {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String text;

    @Column(unique = true, nullable = false)
    private String website;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
