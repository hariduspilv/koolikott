package ee.hm.dop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TranslationObject {

    private String translation;
    private String translationKey;
    private String languageKey;

    public long getTranslationWithId() {
        if (languageKey.equalsIgnoreCase("ET") || languageKey.equalsIgnoreCase("est") )
            return 1L;
        else if (languageKey.equalsIgnoreCase("RU")) {
            return 2L;
        } else if (languageKey.equalsIgnoreCase("EN")) {
            return 3L;
        } else return 0;
    }
}
