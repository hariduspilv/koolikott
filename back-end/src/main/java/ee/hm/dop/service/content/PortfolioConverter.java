package ee.hm.dop.service.content;

import ee.hm.dop.dao.OriginalPictureDao;
import ee.hm.dop.model.OriginalPicture;
import ee.hm.dop.model.Portfolio;

import javax.inject.Inject;

public class PortfolioConverter {

    @Inject
    private OriginalPictureDao originalPictureDao;

    public Portfolio getPortfolioWithAllowedFieldsOnCreate(Portfolio portfolio) {
        Portfolio safePortfolio = new Portfolio();
        return commonConvert(safePortfolio, portfolio, true);
    }

    public Portfolio setPortfolioUpdatableFields(Portfolio to, Portfolio from) {
        commonConvert(to, from, false);
        to.setVisibility(from.getVisibility());
        return to;
    }

    private Portfolio commonConvert(Portfolio to, Portfolio from, boolean create) {
        to.setTitle(from.getTitle());
        to.setSummary(from.getSummary());
        to.setTags(from.getTags());
        to.setTargetGroups(from.getTargetGroups());
        to.setTaxons(from.getTaxons());
        to.setChapters(from.getChapters());
        to.setPicture(from.getPicture());
        if (from.getPicture() != null) {
            if (from.getPicture().getId() == null && create) {
                to.setPicture(null);
            } else {
                OriginalPicture originalPicture = originalPictureDao.findById(from.getPicture().getId());
                to.getPicture().setData(originalPicture.getData());
                to.getPicture().setName(originalPicture.getName());
            }
        }
        return to;
    }
}
