package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.TranslationGroup;

public class TranslationDAO {

    public static enum TranslationGroupEnum {
        EST, RUS, ENG
    }

    @Inject
    private EntityManager entityManager;

    @Inject
    private LanguageDAO languageDAO;

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
        } catch (NoResultException e) {
            e.printStackTrace();
        }

        return translationKey;
    }

    public String getTranslationByKeyAndLangcode(String translationKey, Long langCode) {

        String translation = null;
        Query query = entityManager.createNativeQuery("SELECT t.translation FROM Translation t WHERE lower(t.translationKey) = :translationKey AND t.translationGroup = :translationGroup");

        try {
            translation = (String) query.setParameter("translationKey", translationKey).setParameter("translationGroup", langCode).getSingleResult();
        } catch (NoResultException ex) {
            // ignore
        }

        return translation;
    }
}
