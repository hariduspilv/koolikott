package ee.hm.dop.service.content;

import ee.hm.dop.model.Portfolio;

public class PortfolioConverter {

    public Portfolio getPortfolioWithAllowedFieldsOnCreate(Portfolio portfolio) {
        Portfolio safePortfolio = new Portfolio();
        return commonConvert(safePortfolio, portfolio);
    }

    public Portfolio setPortfolioUpdatableFields(Portfolio to, Portfolio from) {
        commonConvert(to, from);
        to.setVisibility(from.getVisibility());
        return to;
    }

    private Portfolio commonConvert(Portfolio to, Portfolio from) {
        to.setTitle(from.getTitle());
        to.setSummary(from.getSummary());
        to.setTags(from.getTags());
        to.setTargetGroups(from.getTargetGroups());
        to.setTaxons(from.getTaxons());
        to.setChapters(from.getChapters());
        to.setPicture(from.getPicture());
        return to;
    }
}
