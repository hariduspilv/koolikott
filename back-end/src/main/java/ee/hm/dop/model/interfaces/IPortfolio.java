package ee.hm.dop.model.interfaces;

import ee.hm.dop.model.enums.Visibility;
import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;

import static org.apache.commons.lang3.StringUtils.left;

/**
 * a way to unify Portfolio.class and ReducedPortfolio.class
 */
public interface IPortfolio extends ILearningObject {
    Visibility getVisibility();

    String getTitle();

    default String getTitleForUrl() {
        String title = getTitle();
        if (StringUtils.isBlank(title))
            return new String();
        return left(Normalizer.normalize(getTitle(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .replaceAll("[\\s+\\p{P}\\p{S}]", "_"), 20);
    }
}
