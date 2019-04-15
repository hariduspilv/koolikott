package ee.hm.dop.dao;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.PortfolioMaterial;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigInteger;
import java.util.List;

public class PortfolioMaterialDao extends AbstractDao<PortfolioMaterial> {

    public void connectMaterialToPortfolio(Material material, Portfolio portfolio) {
        getEntityManager()
                .createNativeQuery("INSERT INTO PortfolioMaterial (portfolio,material) VALUES (:portfolio,:material)")
                .setParameter("portfolio", portfolio)
                .setParameter("material", material)
                .executeUpdate();
    }

    public List<PortfolioMaterial> findAllPortfolioMaterialsByPortfolio(Long portfolio) {
        return getEntityManager()
                .createNativeQuery("" +
                        "SELECT pm.* FROM PortfolioMaterial pm " +
                        "WHERE pm.portfolio =:portfolio", entity())
                .setParameter("portfolio", portfolio)
                .getResultList();
    }

    public boolean materialToPortfolioConnected(Material material, Portfolio portfolio) {
        List<Long> portfolioList = getEntityManager()
                .createQuery("SELECT pm.portfolio.id FROM PortfolioMaterial pm WHERE pm.material =:material")
                .setParameter("material", material)
                .getResultList();

        return portfolioList.contains(portfolio.getId());
    }

    public boolean hasData() {
        Boolean value = (Boolean) getEntityManager()
                .createNativeQuery("select exists(select * from PortfolioMaterial) as exi")
                .getSingleResult();
        return Boolean.TRUE.equals(value);
    }

    public void deleteNotExistingMaterialIds(Long portfolioId, Long materialId) {
        getEntityManager().createNativeQuery("DELETE pm FROM PortfolioMaterial pm " +
                "WHERE pm.portfolio =:portfolioId AND pm.material =:materialId")
                .setParameter("portfolioId", portfolioId)
                .setParameter("materialId", materialId)
                .executeUpdate();
    }

    public void deleteNotExistingMaterialIds(Long portfolioId, List<Long> materialId) {
        if (CollectionUtils.isNotEmpty(materialId)) {
            getEntityManager().createNativeQuery("DELETE pm FROM PortfolioMaterial pm " +
                    "WHERE pm.portfolio =:portfolioId AND pm.material in (:materialId)")
                    .setParameter("portfolioId", portfolioId)
                    .setParameter("materialId", materialId)
                    .executeUpdate();
        }
    }

    public void removeDeletedPortfolio(Long portfolioId) {
        getEntityManager().createNativeQuery("DELETE pm FROM PortfolioMaterial pm WHERE pm.portfolio =:portfolioId")
                .setParameter("portfolioId", portfolioId)
                .executeUpdate();
    }
}
