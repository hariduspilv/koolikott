package ee.hm.dop.dao;

import ee.hm.dop.model.Language;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class LanguageDao extends AbstractDao<Language> {

    public Language findByCode(String code) {
        TypedQuery<Language> typedQuery = getEntityManager().createQuery(
                "select l from LanguageTable l left join l.codes c where l.code = :code or c = :code", entity())
                .setParameter("code", code);
        return getSingleResult(typedQuery);
    }

    @Override
    public List<Language> findAll() {
        return getEntityManager()
                .createQuery("FROM LanguageTable l ORDER BY priorityOrder DESC, id ASC", entity())
                .getResultList();
    }
}
