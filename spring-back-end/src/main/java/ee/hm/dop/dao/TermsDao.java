package ee.hm.dop.dao;

import ee.hm.dop.model.Terms;
import ee.hm.dop.model.enums.TermType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TermsDao extends AbstractDao<Terms> {

    public List<Terms> getGdprTerms() {
        return getTermsByType(TermType.GDPR);
    }

    public List<Terms> getTerms() {
        return getTermsByType(TermType.TERM);
    }

    public List<Terms> getTermsByType(TermType type) {
        return entityManager.createQuery("select t from Terms t " +
                "where t.type = :type " +
                "order by t.createdAt desc", entity())
                .setParameter("type", type)
                .setMaxResults(1)
                .getResultList();
    }

}
