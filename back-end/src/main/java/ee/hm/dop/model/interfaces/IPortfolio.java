package ee.hm.dop.model.interfaces;

import ee.hm.dop.model.enums.Visibility;

import java.text.Normalizer;

import static org.apache.commons.lang3.StringUtils.left;

/**
 * a way to unify Portfolio.class and ReducedPortfolio.class
 */
public interface IPortfolio extends ILearningObject{
    Visibility getVisibility();

    String getTitle();

    default String getTitleForUrl() {
        return left(Normalizer.normalize(getTitle(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("\\s+", "_"), 20);
    }
}
