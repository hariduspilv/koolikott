package ee.hm.dop.service.synchronizer;

import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.PortfolioDao;
import ee.hm.dop.dao.PortfolioMaterialDao;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.ChapterBlock;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.PortfolioMaterial;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@Transactional
public class UpdatePortfolioMaterials {

    private static final String MATERIAL_REGEX = "class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"[0-9]*\"";
    private static final String NUMBER_REGEX = "\\d+";
    private static final Logger logger = LoggerFactory.getLogger(UpdatePortfolioMaterials.class);

    @Inject
    private PortfolioMaterialDao portfolioMaterialDao;
    @Inject
    private PortfolioDao portfolioDao;
    @Inject
    private MaterialDao materialDao;


    public synchronized void run() {
        try {
            boolean hasData = portfolioMaterialDao.hasData();
            if (hasData) {
                logger.info("PortfolioMaterials table is not empty, migration will not proceed!");
                return;
            }
            Pattern chapterPattern = Pattern.compile(MATERIAL_REGEX);
            Pattern numberPattern = Pattern.compile(NUMBER_REGEX);
            logger.info("Portfolio materials updating started");

            for (Portfolio portfolio : portfolioDao.findAll()) {
                for (Chapter chapter : portfolio.getChapters()) {
                    for (ChapterBlock block : chapter.getBlocks()) {
                        updatePortfolioMaterials(chapterPattern, numberPattern, portfolio, block);
                    }
                }
            }
            logger.info("Portfolio materials updating ended");
        } catch (Exception e) {
            logger.error("Unexpected error while synchronizing portfolios with materials.", e);
        }
    }

    private void updatePortfolioMaterials(Pattern chapterPattern, Pattern numberPattern, Portfolio portfolio, ChapterBlock block) {
        if (StringUtils.isNotBlank(block.getHtmlContent())) {
            Matcher chapterMatcher = chapterPattern.matcher(block.getHtmlContent());
            while (chapterMatcher.find()) {
                Matcher numberMatcher = numberPattern.matcher(chapterMatcher.group());
                while (numberMatcher.find()) {
                    if (!portfolioMaterialDao.materialToPortfolioConnected(materialDao.findById(Long.valueOf(numberMatcher.group())), portfolio)) {
                        PortfolioMaterial portfolioMaterial = new PortfolioMaterial();
                        portfolioMaterial.setPortfolio(portfolio);
                        portfolioMaterial.setMaterial(materialDao.findById(Long.valueOf(numberMatcher.group())));
                        portfolioMaterialDao.createOrUpdate(portfolioMaterial);
                    }
                }
            }
        }
    }

    @Async
    @Transactional
    public synchronized void runAsync() {
        run();
    }
}
