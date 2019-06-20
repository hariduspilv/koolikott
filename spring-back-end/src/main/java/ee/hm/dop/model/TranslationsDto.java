package ee.hm.dop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TranslationsDto {

    private List<String> translations;
    private String translationKey;
    private List<String> languageKeys;
}
