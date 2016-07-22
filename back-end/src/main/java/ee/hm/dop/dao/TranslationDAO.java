package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.TranslationGroup;

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
}
