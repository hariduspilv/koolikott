package ee.hm.dop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ee.hm.dop.model.interfaces.IPortfolio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "Portfolio")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminPortfolio extends AdminLearningObject implements IPortfolio {

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
