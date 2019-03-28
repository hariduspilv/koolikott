package ee.hm.dop.model;

import javax.persistence.*;

@Entity
public class PortfolioMaterial implements AbstractEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "portfolio", nullable = false)
    private Portfolio portfolio;

    @ManyToOne
    @JoinColumn(name = "material", nullable = false)
    private Material material;

    public PortfolioMaterial() {
    }

    public PortfolioMaterial(Portfolio portfolio, Material material) {
        this.portfolio = portfolio;
        this.material = material;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
