package ee.hm.dop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TranslationObject implements Serializable {

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

    public String transformLanguageKey() {

        if (languageKey.equalsIgnoreCase("ET") || languageKey.equalsIgnoreCase("est") )
            return "est";
        else if (languageKey.equalsIgnoreCase("RU") || languageKey.equalsIgnoreCase("rus") ) {
            return "rus";
        } else if (languageKey.equalsIgnoreCase("EN") || languageKey.equalsIgnoreCase("eng") ) {
            return "eng";
        } else return null;
    }

    public String transformLanguageKey2(Long languageKey) {

        if (languageKey == 1L)
            return "est";
        else if (languageKey == 2L) {
            return "rus";
        } else if (languageKey == 3L) {
            return "eng";
        } else return null;
    }
}
