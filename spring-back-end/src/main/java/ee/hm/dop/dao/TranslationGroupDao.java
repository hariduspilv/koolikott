package ee.hm.dop.dao;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.AbstractDao;
import ee.hm.dop.model.LandingPageString;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.TranslationGroup;
import ee.hm.dop.model.TranslationObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TranslationGroupDao extends AbstractDao<TranslationGroup> {

    public TranslationGroup findTranslationGroupFor(Language language) {
        try {
            return entityManager
                    .createQuery("SELECT tg FROM TranslationGroup tg " +
                            "WHERE tg.language = :language", entity())
                    .setParameter("language", language)
                    .getSingleResult();
        } catch (NoResultException ignored) {
            return null;
        }
    }

    public String getTranslationKeyByTranslation(String translation) {
        try {
            return (String) entityManager
                    .createNativeQuery("SELECT t.translationKey FROM Translation t " +
                            "WHERE lower(t.translation) = :translation")
                    .setParameter("translation", translation.toLowerCase())
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NonUniqueResultException | NoResultException ignored) {
            return null;
        }
    }

    public List<String> getTranslationsForKey(List<String> translationKey) {
        if (CollectionUtils.isEmpty(translationKey)) {
            return Lists.newArrayList();
        }
        List<String> lowercaseKeys = translationKey.stream().map(String::toLowerCase).collect(Collectors.toList());
        try {
            List translations = entityManager
                    .createNativeQuery("SELECT t.translation FROM Translation t " +
                            "WHERE lower(t.translationKey) IN (:translationKey)")
                    .setParameter("translationKey", lowercaseKeys)
                    .getResultList();
            List<String> stringArrayList = new ArrayList<>();
            for (Object object : translations) {
                if (object instanceof String) {
                    String string = (String) object;
                    stringArrayList.add(string);
                } else if (object instanceof Clob) {
                    Clob clob = (Clob) object;
                    stringArrayList.add(clob.getSubString(1, (int) clob.length()));
                }
            }
            return stringArrayList;
        } catch (NonUniqueResultException | NoResultException | SQLException ignored) {
            return null;
        }
    }


    public String getTranslationByKeyAndLangcode(String translationKey, Long langCode) {
        try {
            Object result = entityManager
                    .createNativeQuery("SELECT t.translation FROM Translation t " +
                            "WHERE t.translationKey = :translationKey " +
                            "AND t.translationGroup = :translationGroup")
                    .setParameter("translationKey", translationKey)
                    .setParameter("translationGroup", langCode)
                    .getSingleResult();
            if (result instanceof String){
                return (String) result;
            }
            Clob singleResult = (Clob) result;
            return singleResult.getSubString(1, (int) singleResult.length());
        } catch (RuntimeException | SQLException ignored) {
            return null;
        }
    }

    public TranslationObject getTranslationByKeyAndLangcode(Long langCode, String translationKey) {
        TranslationObject to = new TranslationObject();
        try {
            Object result = entityManager
                    .createNativeQuery("SELECT t.translation FROM Translation t " +
                            "WHERE t.translationKey = :translationKey " +
                            "AND t.translationGroup = :translationGroup")
                    .setParameter("translationKey", translationKey)
                    .setParameter("translationGroup", langCode)
                    .getSingleResult();
            if (result instanceof String) {
                to.setTranslation((String) result);
                to.setTranslationKey(translationKey);
                to.setLanguageKey(to.transformLanguageKey2(langCode));
                return to;
            } else {
                return null;
            }

        } catch (RuntimeException e) {
            return null;
        }
    }

    public void updateTranslation(String translation, String translationKey, String languageKey) {
        getEntityManager().createNativeQuery("" +
                "UPDATE Translation\n" +
                "SET translation = :translation\n" +
                "WHERE translationKey = :translationKey\n" +
                "  AND translationGroup = (select tg.id from TranslationGroup tg join LanguageTable L on tg.lang = L.id where L.code = :languageKey)")
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
