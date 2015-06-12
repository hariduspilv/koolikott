package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.Language;

public class LanguageDAO {

    @Inject
    private EntityManager entityManager;

    public Language findByCode(String code) {
        TypedQuery<Language> findByCode = entityManager.createNamedQuery("Language.findByCode", Language.class);

        Language language = null;
        try {
            language = findByCode.setParameter("code", code).getSingleResult();
        } catch (NoResultException ex) {
            // ignore
        }

        return language;
    }
}
