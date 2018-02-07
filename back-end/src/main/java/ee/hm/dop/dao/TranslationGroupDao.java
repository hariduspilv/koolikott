package ee.hm.dop.dao;

import com.google.common.collect.Lists;
import ee.hm.dop.dao.AbstractDao;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.TranslationGroup;
import org.apache.commons.collections.CollectionUtils;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            Clob singleResult = (Clob) entityManager
                    .createNativeQuery("SELECT t.translation FROM Translation t " +
                            "WHERE t.translationKey = :translationKey " +
                            "AND t.translationGroup = :translationGroup")
                    .setParameter("translationKey", translationKey)
                    .setParameter("translationGroup", langCode)
                    .getSingleResult();
            return singleResult.getSubString(1, (int) singleResult.length());
        } catch (RuntimeException | SQLException ignored) {
            return null;
        }
    }
}
