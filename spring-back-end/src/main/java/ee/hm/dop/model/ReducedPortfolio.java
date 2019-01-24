package ee.hm.dop.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ee.hm.dop.model.interfaces.IPortfolio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Portfolio")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReducedPortfolio extends ReducedLearningObject implements IPortfolio {

    @Column(nullable = false)
    private String title;

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
