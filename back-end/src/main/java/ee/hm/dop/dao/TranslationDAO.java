package ee.hm.dop.dao;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.TranslationGroup;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.sql.Clob;
import java.sql.SQLException;

public class TranslationDAO {

    @Inject
    private EntityManager entityManager;

    public TranslationGroup findTranslationGroupFor(Language language) {
        try {
            return entityManager
                    .createQuery("SELECT tg FROM TranslationGroup tg " +
                            "WHERE tg.language = :language", TranslationGroup.class)
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
