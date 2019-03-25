package ee.hm.dop.service.content;

import ee.hm.dop.config.guice.GuiceInjector;
import ee.hm.dop.dao.MaterialDao;
import ee.hm.dop.dao.OriginalPictureDao;
import ee.hm.dop.dao.PortfolioMaterialDao;
import ee.hm.dop.model.Chapter;
import ee.hm.dop.model.ChapterBlock;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.PortfolioMaterial;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PortfolioConverter {

    @Inject
    private OriginalPictureDao originalPictureDao;

    private static final String MATERIAL_REGEX = "class=\"chapter-embed-card chapter-embed-card--material\" data-id=\"[0-9]*\"";
    private static final String NUMBER_REGEX = "\\d+";

    private PortfolioMaterialDao newPortfolioMaterialDao = newPortfolioMaterialDao();
    private MaterialDao newMaterialDao = newMaterialDao();
    private Pattern chapterPattern = Pattern.compile(MATERIAL_REGEX);
    private Pattern numberPattern = Pattern.compile(NUMBER_REGEX);

    public Portfolio setFieldsToNewPortfolio(Portfolio portfolio) {
        Portfolio safePortfolio = new Portfolio();
        return setCommonFields(safePortfolio, portfolio);
    }

    public Portfolio setFieldsToExistingPortfolio(Portfolio to, Portfolio from) {
        setCommonFields(to, from);
        setUpdateFields(to, from);
        return to;
    }

    private void setUpdateFields(Portfolio to, Portfolio from) {
        if (changesToPublic(to, from)) to.setPublishedAt(DateTime.now());
        to.setVisibility(from.getVisibility());
        to.setPublicationConfirmed(from.isPublicationConfirmed());
    }

    private boolean changesToPublic(Portfolio to, Portfolio from) {
        return from.getVisibility().isPublic() && to.getVisibility().isNotPublic();
    }

    private Portfolio setCommonFields(Portfolio to, Portfolio from) {
        to.setTitle(from.getTitle());
        to.setSummary(from.getSummary());
        to.setTags(from.getTags());
        to.setTargetGroups(from.getTargetGroups());
        to.setTaxons(from.getTaxons());

        if (from.getChapters() != null && from.getChapters() != null)  {

            List<Chapter> toChapters = new ArrayList<>(to.getChapters());
            List<Chapter> fromChapters = new ArrayList<>(from.getChapters());
            if (toChapters.size() > fromChapters.size()) {
                for (Chapter c : toChapters) {
                    for (Chapter d : fromChapters) {
                        if (c.getId() != d.getId()) {
                            for (Chapter chapter : to.getChapters()) {
                                if (chapter.getBlocks() != null) {
                                    for (ChapterBlock block : chapter.getBlocks()) {
                                        if (StringUtils.isNotBlank(block.getHtmlContent())) {
                                            Matcher matcher = chapterPattern.matcher(block.getHtmlContent());
                                            while (matcher.find()) {
                                                Matcher numberMatcher = numberPattern.matcher(matcher.group());
                                                while (numberMatcher.find()) {
                                                    Material material = newMaterialDao.findById(Long.valueOf(numberMatcher.group()));
                                                    PortfolioMaterial portfolioMaterial = newPortfolioMaterialDao.findByField("material", material);
                                                    if (portfolioMaterial != null) {
                                                        newPortfolioMaterialDao.remove(portfolioMaterial);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        to.setChapters(from.getChapters());
        to.setPicture(from.getPicture());
        to.setLicenseType(from.getLicenseType());
        if (from.getPicture() != null) {
            if (from.getPicture().getId() == null) {
                to.setPicture(null);
            } else {
                OriginalPicture originalPicture = originalPictureDao.findById(from.getPicture().getId());
                to.getPicture().setData(originalPicture.getData());
                to.getPicture().setName(originalPicture.getName());
            }
        }
        return to;
    }

    private PortfolioMaterialDao newPortfolioMaterialDao() {
        return GuiceInjector.getInjector().getInstance(PortfolioMaterialDao.class);
    }

    private MaterialDao newMaterialDao() {
        return GuiceInjector.getInjector().getInstance(MaterialDao.class);
    }
}
