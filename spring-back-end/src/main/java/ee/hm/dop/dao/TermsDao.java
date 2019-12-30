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

    public List<Terms> getUserTerms() {
        return getTermsByType(TermType.USAGE);
    }

    public List<Terms> getTermsByType(TermType type) {
        return entityManager.createQuery("select t from Terms t " +
                "where t.type = :type " +
                "and t.deleted = false " +
                "order by t.createdAt desc", entity())
                .setParameter("type", type)
                .setMaxResults(1)
                .getResultList();
    }

    public void deletePreviousTerms(TermType type) {
        entityManager.createQuery("update Terms " +
                "set deleted = 1 " +
                "where type = :type")
                .setParameter("type", type)
                .executeUpdate();
    }
}
