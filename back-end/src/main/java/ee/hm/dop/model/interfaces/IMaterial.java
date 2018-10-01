package ee.hm.dop.model.interfaces;


import ee.hm.dop.model.LanguageString;
import org.apache.commons.collections4.CollectionUtils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.left;

/**
 * a way to unify Material.class and ReducedMaterial.class
 */
public interface IMaterial extends ILearningObject{

    List<LanguageString> getTitles();

    default List<LanguageString> getTitlesForUrl() {
        List<LanguageString> titles = getTitles();
        if (CollectionUtils.isEmpty(titles)) return new ArrayList<>();
        return titles.stream()
                .map(t -> new LanguageString(t.getLanguage(), formatTextForUrl(t.getText())))
                .collect(Collectors.toList());
    }

    default String formatTextForUrl(String text) {
        return left(Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("[\\s+\\p{P}\\p{S}]", "_"), 20);
    }
}
