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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class UpdatePortfolioMaterials extends DopDaemonProcess {

    public static final String MATERIAL_REGEX = "class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"[0-9]*\"";
    public static final String NUMBER_REGEX = "\\d+";

    @Inject
    private PortfolioMaterialDao portfolioMaterialDao;
    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private MaterialDao materialDao;

    private static final Logger logger = LoggerFactory.getLogger(UpdatePortfolioMaterials.class);

    @Override
    public synchronized void run() {
        try {
            beginTransaction();

            PortfolioMaterialDao newPortfolioMaterialDao = newPortfolioMaterialDao();
            Pattern chapterPattern = Pattern.compile(MATERIAL_REGEX);
            Pattern numberPattern = Pattern.compile(NUMBER_REGEX);

            for (Portfolio portfolio : portfolioDao.findAll()) {
                for (Chapter chapter : portfolio.getChapters()) {
                    for (ChapterBlock block : chapter.getBlocks()) {
                        if (StringUtils.isNotBlank(block.getHtmlContent())) {
                            Matcher matcher = chapterPattern.matcher(block.getHtmlContent());
                            while (matcher.find()) {
                                Matcher numberMatcher = numberPattern.matcher(matcher.group());
                                while (numberMatcher.find()) {
                                    if (portfolioMaterialDao.materialToPortfolioConnected(materialDao.findById(Long.valueOf(numberMatcher.group())), portfolio) == false) {
                                        PortfolioMaterial portfolioMaterial = new PortfolioMaterial();
                                        portfolioMaterial.setPortfolio(portfolio);
                                        portfolioMaterial.setMaterial(materialDao.findById(Long.valueOf(numberMatcher.group())));
                                        newPortfolioMaterialDao.createOrUpdate(portfolioMaterial);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Unexpected error while synchronizing portfolios with materials.", e);
        } finally {
            closeEntityManager();
        }
    }

    private PortfolioMaterialDao newPortfolioMaterialDao() {
        return GuiceInjector.getInjector().getInstance(PortfolioMaterialDao.class);
    }
}
