package ee.hm.dop.dao;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.PortfolioMaterial;

import java.util.List;

public class PortfolioMaterialDao extends AbstractDao<PortfolioMaterial> {

    public void connectMaterialToPortfolio(Material material, Portfolio portfolio) {
        getEntityManager()
                .createNativeQuery("INSERT INTO PortfolioMaterial (portfolio,material) VALUES (:portfolio,:material)")
                .setParameter("portfolio", portfolio)
                .setParameter("material", material)
                .executeUpdate();
    }

    public boolean materialToPortfolioConnected(Material material, Portfolio portfolio) {
        List<Portfolio> portfolioList = getEntityManager()
                .createQuery("SELECT pm.portfolio FROM PortfolioMaterial pm WHERE pm.material =: material")
                .setParameter("material", material)
                .getResultList();

        return portfolioList.isEmpty();

    }

}
