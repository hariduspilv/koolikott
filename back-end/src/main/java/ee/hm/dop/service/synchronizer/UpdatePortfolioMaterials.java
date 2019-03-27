package ee.hm.dop.service.synchronizer;

import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.PortfolioMaterialDao;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.ChapterBlock;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.PortfolioMaterial;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class UpdatePortfolioMaterials extends DopDaemonProcess {

    private static final String MATERIAL_REGEX = "class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"[0-9]*\"";
    private static final String NUMBER_REGEX = "\\d+";
    private static final Logger logger = LoggerFactory.getLogger(UpdatePortfolioMaterials.class);

    @Override
    public synchronized void run() {
        try {

            PortfolioMaterialDao newPortfolioMaterialDao = newPortfolioMaterialDao();
            PortfolioDao newPortfolioDao = newPortfolioDao();
            MaterialDao newMaterialDao = newMaterialDao();

            boolean hasData = newPortfolioMaterialDao.hasData();
            if (hasData) {
                logger.info("PortfolioMaterials table is not empty, migration will not proceed!");
                return;
            }

            beginTransaction();
            Pattern chapterPattern = Pattern.compile(MATERIAL_REGEX);
            Pattern numberPattern = Pattern.compile(NUMBER_REGEX);
            logger.info("Portfolio materials updating started");

            for (Portfolio portfolio : newPortfolioDao.getAllPortfoliosDeletedExcluded()) {
                for (Chapter chapter : portfolio.getChapters()) {
                    if (chapter.getBlocks() != null) {
                        for (ChapterBlock block : chapter.getBlocks()) {
                            if (StringUtils.isNotBlank(block.getHtmlContent())) {
                                Matcher chapterMatcher = chapterPattern.matcher(block.getHtmlContent());
                                while (chapterMatcher.find()) {
                                    Matcher numberMatcher = numberPattern.matcher(chapterMatcher.group());
                                    while (numberMatcher.find()) {
                                        if (!newPortfolioMaterialDao.materialToPortfolioConnected(newMaterialDao.findById(Long.valueOf(numberMatcher.group())), portfolio)) {
                                            PortfolioMaterial portfolioMaterial = newPortfolioMaterial();
                                            portfolioMaterial.setPortfolio(portfolio);
                                            portfolioMaterial.setMaterial(newMaterialDao.findById(Long.valueOf(numberMatcher.group())));
                                            newPortfolioMaterialDao.createOrUpdate(portfolioMaterial);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            closeTransaction();
            logger.info("Portfolio materials updating ended");

        } catch (Exception e) {
            logger.error("Unexpected error while synchronizing portfolios with materials.", e);
        }
        finally {
            closeEntityManager();
        }
    }

    private PortfolioMaterial newPortfolioMaterial() {
        return GuiceInjector.getInjector().getInstance(PortfolioMaterial.class);
    }

    private PortfolioDao newPortfolioDao() {
        return GuiceInjector.getInjector().getInstance(PortfolioDao.class);
    }

    private MaterialDao newMaterialDao() {
        return GuiceInjector.getInjector().getInstance(MaterialDao.class);
    }

    private PortfolioMaterialDao newPortfolioMaterialDao() {
        return GuiceInjector.getInjector().getInstance(PortfolioMaterialDao.class);
    }
}
