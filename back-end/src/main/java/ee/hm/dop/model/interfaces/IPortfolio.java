package ee.hm.dop.model.interfaces;

import ee.hm.dop.model.enums.Visibility;

/**
 * a way to unify Portfolio.class and ReducedPortfolio.class
 */
public interface IPortfolio extends ILearningObject{
    Visibility getVisibility();
}
