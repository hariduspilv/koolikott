package ee.hm.dop.model.interfaces;


import ee.hm.dop.model.LanguageString;
import ee.hm.dop.utils.tokenizer.TitleUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * a way to unify Material.class and ReducedMaterial.class
 */
public interface IMaterial extends ILearningObject{

    List<LanguageString> getTitles();

    default List<LanguageString> getTitlesForUrl() {
        if (CollectionUtils.isEmpty(getTitles())) return new ArrayList<>();
        return getTitles().stream()
                .map(t -> new LanguageString(t.getLanguage(), formatTextForUrl(t.getText())))
                .collect(Collectors.toList());
    }

    default String formatTextForUrl(String text) {
        return TitleUtils.makeEncodingFriendly(text);
    }
}
