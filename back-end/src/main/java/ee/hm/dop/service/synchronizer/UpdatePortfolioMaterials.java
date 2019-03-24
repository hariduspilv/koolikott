package ee.hm.dop.service.synchronizer;

import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.PortfolioMaterialDao;
import ee.hm.dop.model.PortfolioMaterial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class UpdatePortfolioMaterials extends DopDaemonProcess {

    private static final String MATERIAL_REGEX = "class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"[0-9]*\"";
    private static final String NUMBER_REGEX = "\\d+";

//    @Inject
//    private PortfolioMaterialDao portfolioMaterialDao;
//    @Inject
//    private PortfolioDao portfolioDao;
//    @Inject
//    private MaterialDao materialDao;

    private static final Logger logger = LoggerFactory.getLogger(UpdatePortfolioMaterials.class);

    @Override
    public synchronized void run() {
        try {
            beginTransaction();

            PortfolioMaterialDao newPortfolioMaterialDao = newPortfolioMaterialDao();
            PortfolioDao newPortfolioDao = newPortfolioDao();
            MaterialDao newMaterialDao = newMaterialDao();

            newPortfolioMaterialDao.updatePortfolioMaterials();

//            Pattern chapterPattern = Pattern.compile(MATERIAL_REGEX);
//            Pattern numberPattern = Pattern.compile(NUMBER_REGEX);

//            for (Portfolio portfolio : newPortfolioDao.findAll()) {
//                for (Chapter chapter : portfolio.getChapters()) {
//                    for (ChapterBlock block : chapter.getBlocks()) {
//                        if (StringUtils.isNotBlank(block.getHtmlContent())) {
//                            Matcher chapterMatcher = chapterPattern.matcher(block.getHtmlContent());
//                            while (chapterMatcher.find()) {
//                                Matcher numberMatcher = numberPattern.matcher(chapterMatcher.group());
//                                while (numberMatcher.find()) {
//                                    if (!newPortfolioMaterialDao.materialToPortfolioConnected(newMaterialDao.findById(Long.valueOf(numberMatcher.group())), portfolio)) {
////                                        PortfolioMaterial portfolioMaterial = new PortfolioMaterial();
//                                        PortfolioMaterial portfolioMaterial = newPortfolioMaterial();
//                                        portfolioMaterial.setPortfolio(portfolio);
//                                        portfolioMaterial.setMaterial(newMaterialDao.findById(Long.valueOf(numberMatcher.group())));
//                                        newPortfolioMaterialDao.createOrUpdate(portfolioMaterial);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }

        } catch (Exception e) {
            logger.error("Unexpected error while synchronizing portfolios with materials.", e);
        } finally {
            closeEntityManager();
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
