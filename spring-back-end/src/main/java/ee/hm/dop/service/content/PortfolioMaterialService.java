package ee.hm.dop.service.content;

import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.PortfolioMaterialDao;
import ee.hm.dop.model.*;
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
    public static final String DELETED_MATERIAL_REGEX = "class=\"chapter-embed-card chapter-embed-card--material is-deleted\" data-id=\"[0-9]*\"";
    public static final String MATERIAL_FLOAT_RIGHT_REGEX = "class=\"chapter-embed-card chapter-embed-card--material chapter-embed-card--float-right\" data-id=\"[0-9]*\"";
    public static final String MATERIAL_DELETED_FLOAT_RIGHT_REGEX = "class=\"chapter-embed-card chapter-embed-card--material chapter-embed-card--float-right is-deleted\" data-id=\"[0-9]*\"";
    public static final String MATERIAL_FLOAT_LEFT_REGEX = "class=\"chapter-embed-card chapter-embed-card--material chapter-embed-card--float-left\" data-id=\"[0-9]*\"";
    public static final String MATERIAL_DELETED_FLOAT_LEFT_REGEX = "class=\"chapter-embed-card chapter-embed-card--material chapter-embed-card--float-left is-deleted\" data-id=\"[0-9]*\"";

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
            if (material != null) {
                portfolioMaterialDao.createOrUpdate(new PortfolioMaterial(portfolio, material));
            }
        }
    }

    private List<Long> frontMaterialIds(Portfolio portfolio) {
        Set<Long> frontIds = new HashSet<>();
        Pattern materialPattern = Pattern.compile(MATERIAL_REGEX);
        Pattern deletedMaterialPattern = Pattern.compile(DELETED_MATERIAL_REGEX);
        Pattern floatedMaterialRightPattern = Pattern.compile(MATERIAL_FLOAT_RIGHT_REGEX);
        Pattern floatedDeletedMaterialRightPattern = Pattern.compile(MATERIAL_DELETED_FLOAT_RIGHT_REGEX);
        Pattern floatedMaterialLeftPattern = Pattern.compile(MATERIAL_FLOAT_LEFT_REGEX);
        Pattern floatedDeletedMaterialLeftPattern = Pattern.compile(MATERIAL_DELETED_FLOAT_LEFT_REGEX);
        Pattern numberPattern = Pattern.compile(NUMBER_REGEX);

        if (portfolio.getChapters() != null) {
            portfolio.getChapters().stream()
                    .filter(chapter -> chapter.getBlocks() != null)
                    .forEach(chapter -> chapter.getBlocks().stream()
                            .filter(block -> StringUtils.isNotBlank(block.getHtmlContent()))
                            .forEach(block -> {
                                findMaterialsNotDeleted(frontIds, materialPattern, numberPattern, block);
                                findMaterialsDeleted(frontIds, deletedMaterialPattern, numberPattern, block);
                                findMaterialsFloatedRight(frontIds, floatedMaterialRightPattern, numberPattern, block);
                                findMaterialsDeletedFloatedRight(frontIds, floatedDeletedMaterialRightPattern, numberPattern, block);
                                findMaterialsFloatedLeft(frontIds, floatedMaterialLeftPattern, numberPattern, block);
                                findMaterialsDeletedFloatedLeft(frontIds, floatedDeletedMaterialLeftPattern, numberPattern, block);
                            }));
        }
        return new ArrayList<>(frontIds);
    }

    private void findMaterialsNotDeleted(Set<Long> frontIds, Pattern materialPattern, Pattern numberPattern, ChapterBlock block) {
        Matcher materialMatcher = materialPattern.matcher(block.getHtmlContent());
        while (materialMatcher.find()) {
            Matcher numberMatcher = numberPattern.matcher(materialMatcher.group());
            if (numberMatcher.find()) {
                frontIds.add(Long.valueOf(numberMatcher.group()));
            }
        }
    }

    private void findMaterialsDeleted(Set<Long> frontIds, Pattern deletedMaterialPattern, Pattern numberPattern, ChapterBlock block) {
        Matcher deletedMaterialMatcher = deletedMaterialPattern.matcher(block.getHtmlContent());
        while (deletedMaterialMatcher.find()) {
            Matcher deletedNumberMatcher = numberPattern.matcher(deletedMaterialMatcher.group());
            if (deletedNumberMatcher.find()) {
                frontIds.add(Long.valueOf(deletedNumberMatcher.group()));
            }
        }
    }

    private void findMaterialsFloatedRight(Set<Long> frontIds, Pattern floatedMaterialPattern, Pattern numberPattern, ChapterBlock block) {
        Matcher floatedMaterialRightMatcher = floatedMaterialPattern.matcher(block.getHtmlContent());
        while (floatedMaterialRightMatcher.find()) {
            Matcher floatedRightNumberMatcher = numberPattern.matcher(floatedMaterialRightMatcher.group());
            if (floatedRightNumberMatcher.find()) {
                frontIds.add(Long.valueOf(floatedRightNumberMatcher.group()));
            }
        }
    }

    private void findMaterialsDeletedFloatedRight(Set<Long> frontIds, Pattern floatedDeletedMaterialPattern, Pattern numberPattern, ChapterBlock block) {
        Matcher floatedDeletedMaterialRightMatcher = floatedDeletedMaterialPattern.matcher(block.getHtmlContent());
        while (floatedDeletedMaterialRightMatcher.find()) {
            Matcher floatedDeletedRightNumberMatcher = numberPattern.matcher(floatedDeletedMaterialRightMatcher.group());
            if (floatedDeletedRightNumberMatcher.find()) {
                frontIds.add(Long.valueOf(floatedDeletedRightNumberMatcher.group()));
            }
        }
    }

    private void findMaterialsFloatedLeft(Set<Long> frontIds, Pattern floatedMaterialLeftPattern, Pattern numberPattern, ChapterBlock block) {
        Matcher floatedMaterialLeftMatcher = floatedMaterialLeftPattern.matcher(block.getHtmlContent());
        while (floatedMaterialLeftMatcher.find()) {
            Matcher floatedMaterialLeftNumberMatcher = numberPattern.matcher(floatedMaterialLeftMatcher.group());
            if (floatedMaterialLeftNumberMatcher.find()) {
                frontIds.add(Long.valueOf(floatedMaterialLeftNumberMatcher.group()));
            }
        }
    }

    private void findMaterialsDeletedFloatedLeft(Set<Long> frontIds, Pattern floatedDeletedMaterialLeftPattern, Pattern numberPattern, ChapterBlock block) {
        Matcher floatedDeletedMaterialLeftMatcher = floatedDeletedMaterialLeftPattern.matcher(block.getHtmlContent());
        while (floatedDeletedMaterialLeftMatcher.find()) {
            Matcher floatedDeletedMaterialLeftNumberMatcher = numberPattern.matcher(floatedDeletedMaterialLeftMatcher.group());
            if (floatedDeletedMaterialLeftNumberMatcher.find()) {
                frontIds.add(Long.valueOf(floatedDeletedMaterialLeftNumberMatcher.group()));
            }
        }
    }

}
