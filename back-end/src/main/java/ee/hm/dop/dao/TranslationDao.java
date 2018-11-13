package ee.hm.dop.dao;

import ee.hm.dop.model.LandingPageString;
import ee.hm.dop.model.Translation;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class TranslationDao extends AbstractDao<Translation> {

    public void updateTranslation(String translation, String translationKey, String languageKey) {
        getEntityManager().createNativeQuery("" +
                "UPDATE Translation\n" +
                "SET translation = :translation\n" +
                "WHERE translationKey = :translationKey\n" +
                "  AND translationGroup = (select tg.id from TranslationGroup tg join LanguageTable L on tg.lang = L.id where L.code = :languageKey)", Translation.class)
                .setParameter("translation", translation)
                .setParameter("translationKey", translationKey)
                .setParameter("languageKey", languageKey)
                .executeUpdate();

    }

    public List<LandingPageString> getTranslations(String translationKey) {
        List<Object[]> resultList = getEntityManager().createNativeQuery("\n" +
                "select code, translation\n" +
                "from TranslationGroup tg\n" +
                "       join LanguageTable L on tg.lang = L.id\n" +
                "       join Translation T on tg.id = T.translationGroup\n" +
                "where T.translationKey = :translationKey")
                .setParameter("translationKey", translationKey)
                .getResultList();

        return resultList.stream()
                .map(objects -> new LandingPageString((String) objects[0], getTranslationFromMysqlTEXT(objects[1])))
                .collect(Collectors.toList());
    }

    private String getTranslationFromMysqlTEXT(Object translation) {
        if (translation instanceof String) {
            return (String) translation;
        } else if (translation instanceof Clob) {
            try {
                Clob clob = (Clob) translation;
                return clob.getSubString(1, (int) clob.length());
            } catch (SQLException ignored) {
            }
        }
        return "";
    }

}
