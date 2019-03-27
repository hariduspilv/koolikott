package ee.hm.dop.service.content;

import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.PortfolioMaterialDao;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.ChapterBlock;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.PortfolioMaterial;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PortfolioMaterialService {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioMaterialService.class);

    public static final String MATERIAL_REGEX = "class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"[0-9]*\"";
    public static final String NUMBER_REGEX = "\\d+";

    @Inject
    private PortfolioMaterialDao portfolioMaterialDao;
    @Inject
    private MaterialDao materialDao;

    public void save(Portfolio portfolio) {
        List<Long> materialIds = frontMaterialIds(portfolio);
        saveNew(portfolio, materialIds);
    }

    public void update(Portfolio portfolio) {
        List<PortfolioMaterial> existingPortfolioMaterials = portfolioMaterialDao.findAllPortfolioMaterialsByPortfolio(portfolio.getId());
        List<Long> dbIds = existingPortfolioMaterials.stream().map(PortfolioMaterial::getMaterial).map(LearningObject::getId).collect(Collectors.toList());
        List<Long> frontIds = frontMaterialIds(portfolio);

        List<Long> newToSave = new ArrayList<>();
        for (Long fromFrontId : frontIds) {
            if (!dbIds.contains(fromFrontId)) {
                newToSave.add(fromFrontId);
            }
        }
        List<Long> oldToRemove = new ArrayList<>();
        for (Long dbId : dbIds) {
            if (!frontIds.contains(dbId)) {
                oldToRemove.add(dbId);
            }
        }

        saveNew(portfolio, newToSave);
        deleteOld(portfolio, oldToRemove);
    }

    public void delete(Portfolio portfolio){
        portfolioMaterialDao.removeDeletedPortfolio(portfolio.getId());
    }

    private void deleteOld(Portfolio portfolio, List<Long> oldToRemove) {
        portfolioMaterialDao.deleteNotExistingMaterialIds(portfolio.getId(), oldToRemove);
    }

    private void saveNew(Portfolio portfolio, List<Long> newToSave) {
        for (Long materialId : newToSave) {
            Material material = materialDao.findById(materialId);
            portfolioMaterialDao.createOrUpdate(new PortfolioMaterial(portfolio, material));
        }
    }

    private List<Long> frontMaterialIds(Portfolio portfolio) {
        Set<Long> frontIds = new HashSet<>();
        Pattern chapterPattern = Pattern.compile(MATERIAL_REGEX);
        Pattern numberPattern = Pattern.compile(NUMBER_REGEX);

        for (Chapter chapter : portfolio.getChapters()) {
            if (chapter.getBlocks() != null) {
                for (ChapterBlock block : chapter.getBlocks()) {
                    if (StringUtils.isNotBlank(block.getHtmlContent())) {
                        Matcher matcher = chapterPattern.matcher(block.getHtmlContent());
                        while (matcher.find()) {
                            Matcher numberMatcher = numberPattern.matcher(matcher.group());
                            if (numberMatcher.find()) {
                                frontIds.add(Long.valueOf(numberMatcher.group()));
                            } else {
                                logger.info("Did not find material");
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(frontIds);
    }
}
