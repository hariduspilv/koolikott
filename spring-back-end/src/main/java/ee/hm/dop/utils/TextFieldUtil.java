package ee.hm.dop.utils;

import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;

public class TextFieldUtil {

    public static final String REGEX = "[^\\u0000-\\uFFFF]";
    public static final String REPLACEMENT = "\uFFFD";

    public static void cleanTextFields(Material material) {
        if (material.getTitles() != null)
            material.getTitles().forEach(title -> title.setText(title.getText().replaceAll(REGEX, REPLACEMENT)));

        if (material.getDescriptions() != null)
            material.getDescriptions().forEach(desc -> desc.setText(desc.getText().replaceAll(REGEX, REPLACEMENT)));
    }

    public static void cleanTextFields(Portfolio portfolio) {
        if (portfolio.getTitle() != null)
            portfolio.setTitle(portfolio.getTitle().replaceAll(REGEX, REPLACEMENT));

        if (portfolio.getSummary() != null)
            portfolio.setSummary(portfolio.getSummary().replaceAll(REGEX, REPLACEMENT));
    }
}
