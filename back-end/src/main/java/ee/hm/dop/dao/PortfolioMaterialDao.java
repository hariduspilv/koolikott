package ee.hm.dop.dao;

import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.ChapterBlock;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.PortfolioMaterial;
import ee.hm.dop.service.synchronizer.UpdatePortfolioMaterials;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static ee.hm.dop.utils.DbUtils.closeEntityManager;

public class PortfolioMaterialDao extends AbstractDao<PortfolioMaterial> {

    private static final String MATERIAL_REGEX = "class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"[0-9]*\"";
    private static final String NUMBER_REGEX = "\\d+";

    private static final Logger logger = LoggerFactory.getLogger(PortfolioMaterialDao.class);

//    public Class<PortfolioMaterial> entity() {
//        return PortfolioMaterial.class;
//    }

    public void connectMaterialToPortfolio(Material material, Portfolio portfolio) {
        getEntityManager()
                .createNativeQuery("INSERT INTO PortfolioMaterial (portfolio,material) VALUES (:portfolio,:material)")
                .setParameter("portfolio", portfolio)
                .setParameter("material", material)
                .executeUpdate();
    }

    public boolean materialToPortfolioConnected(Material material, Portfolio portfolio) {
        List<Long> portfolioList = getEntityManager()
                .createQuery("SELECT pm.portfolio.id FROM PortfolioMaterial pm WHERE pm.material =:material")
                .setParameter("material", material)
                .getResultList();

        return portfolioList.contains(portfolio.getId());
    }

    public void updatePortfolioMaterials() {
//        PortfolioMaterialDao newPortfolioMaterialDao = newPortfolioMaterialDao();
        PortfolioDao newPortfolioDao = newPortfolioDao();
        MaterialDao newMaterialDao = newMaterialDao();

        Pattern chapterPattern = Pattern.compile(MATERIAL_REGEX);
        Pattern numberPattern = Pattern.compile(NUMBER_REGEX);

        for (Portfolio portfolio : newPortfolioDao.findAll()) {
            for (Chapter chapter : portfolio.getChapters()) {
                for (ChapterBlock block : chapter.getBlocks()) {
                    if (StringUtils.isNotBlank(block.getHtmlContent())) {
                        Matcher chapterMatcher = chapterPattern.matcher(block.getHtmlContent());
                        while (chapterMatcher.find()) {
                            Matcher numberMatcher = numberPattern.matcher(chapterMatcher.group());
                            while (numberMatcher.find()) {
//                                if (!newPortfolioMaterialDao.materialToPortfolioConnected(newMaterialDao.findById(Long.valueOf(numberMatcher.group())), portfolio)) {
                                PortfolioMaterial portfolioMaterial = new PortfolioMaterial();
//                                    PortfolioMaterial portfolioMaterial = newPortfolioMaterial();
                                portfolioMaterial.setPortfolio(portfolio);
                                portfolioMaterial.setMaterial(newMaterialDao.findById(Long.valueOf(numberMatcher.group())));
//                                    newPortfolioMaterialDao.createOrUpdate(portfolioMaterial);
                                createOrUpdate(portfolioMaterial);
//                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private PortfolioMaterial newPortfolioMaterial() {
        return GuiceInjector.getInjector().getInstance(PortfolioMaterial.class);
    }

    private PortfolioDao newPortfolioDao() {
        return GuiceInjector.getInjector().getInstance(PortfolioDao.class);
    }

    private PortfolioMaterialDao newPortfolioMaterialDao() {
        return GuiceInjector.getInjector().getInstance(PortfolioMaterialDao.class);
    }

    private MaterialDao newMaterialDao() {
        return GuiceInjector.getInjector().getInstance(MaterialDao.class);
    }
}
