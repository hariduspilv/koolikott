package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.TranslationGroup;

import java.sql.Clob;
import java.sql.SQLException;

public class TranslationDAO {

    @Inject
    private EntityManager entityManager;

    public TranslationGroup findTranslationGroupFor(Language language) {
        TypedQuery<TranslationGroup> findByLanguage = entityManager.createQuery(
                "SELECT tg FROM TranslationGroup tg WHERE tg.language = :language", TranslationGroup.class);

        TranslationGroup translationGroup = null;
        try {
            translationGroup = findByLanguage.setParameter("language", language).getSingleResult();
        } catch (NoResultException ex) {
            // ignore
        }

        return translationGroup;
    }

    public String getTranslationKeyByTranslation(String translation) {
        Query query = entityManager.createNativeQuery("SELECT t.translationKey FROM Translation t WHERE lower(t.translation) = :translation");

        String translationKey = null;
        try {
            translationKey = (String) query
                    .setParameter("translation", translation.toLowerCase())
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException ignored) {

        }
        return translationKey;
    }

    public String getTranslationByKeyAndLangcode(String translationKey, Long langCode) {
        try {
            Clob singleResult = ((Clob) entityManager.createNativeQuery("SELECT t.translation FROM Translation t WHERE t.translationKey = :translationKey AND t.translationGroup = :translationGroup")
                    .setParameter("translationKey", translationKey)
                    .setParameter("translationGroup", langCode)
                    .getSingleResult());
            if (singleResult == null){
                return null;
            }
            return singleResult.getSubString(1, (int) singleResult.length());

//            Query query = entityManager.createNativeQuery("SELECT t.translation FROM Translation t WHERE t.translationKey = :translationKey AND t.translationGroup = :translationGroup");
//            return (String) query.setParameter("translationKey", translationKey)
//                    .setParameter("translationGroup", langCode)
//                    .getSingleResult();
        } catch (NoResultException | SQLException ex) {
            // ignore
        }
        return null;
    }
}
