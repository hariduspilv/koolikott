package ee.hm.dop.dao;

import ee.hm.dop.model.Language;
import org.springframework.stereotype.Repository;

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

    public Long findIdByCode(String langCode) {
        if (langCode.equalsIgnoreCase("ET")) {
            langCode = "est";
        } else if (langCode.equalsIgnoreCase("RU")) {
            langCode = "rus";
        } else if (langCode.equalsIgnoreCase("EN")) {
            langCode = "eng";
        }
        return (Long) getEntityManager()
                .createQuery("SELECT l.id FROM LanguageTable l WHERE l.code = :code")
                .setParameter("code", langCode)
                .getSingleResult();
    }

    @Override
    public List<Language> findAll() {
        return getEntityManager()
                .createQuery("FROM LanguageTable l ORDER BY priorityOrder DESC, id ASC", entity())
                .getResultList();
    }
}
