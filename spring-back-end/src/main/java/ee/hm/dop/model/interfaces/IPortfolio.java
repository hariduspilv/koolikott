package ee.hm.dop.model.interfaces;

import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.utils.tokenizer.TitleUtils;

/**
 * a way to unify Portfolio.class and ReducedPortfolio.class
 */
public interface IPortfolio extends ILearningObject {
    Visibility getVisibility();

    String getTitle();

    default String getTitleForUrl() {
        return TitleUtils.makeEncodingFriendly(getTitle());
    }

}
