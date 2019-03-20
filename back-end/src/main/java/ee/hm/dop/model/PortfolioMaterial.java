package ee.hm.dop.model;

import javax.persistence.*;

@Entity
public class PortfolioMaterial implements AbstractEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long portfolioId;

    @Column(nullable = false)
    private Long materialId;

    @Override
    public Long getId() {
        return id;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }
}
