package ee.hm.dop.dao;

import ee.hm.dop.model.Agreement;
import org.joda.time.DateTime;

import java.util.List;
import java.util.stream.Collectors;

public class AgreementDao extends AbstractDao<Agreement> {

    public List<Agreement> getValidAgreements() {
        return getList(entityManager
                .createQuery("select a from Agreement a " +
                        "where a.deleted = false " +
                        "order by a.validFrom desc, a.id desc", entity()));
    }

    public Agreement findLatestAgreement() {
        return getSingleResult(entityManager
                .createQuery("select a from Agreement a " +
                        "where a.validFrom < :validFrom " +
                        "and a.deleted = false " +
                        "order by a.validFrom desc, a.id desc", entity())
                .setParameter("validFrom", DateTime.now())
                .setMaxResults(1));
    }

    public List<Agreement> findMatchingAgreements(Agreement agreement) {
        return getList(entityManager
                .createQuery("select a from Agreement a " +
                        "where a.validFrom = :validFrom " +
                        "and a.version = :version " +
                        "and a.deleted = false", entity())
                .setParameter("validFrom", agreement.getValidFrom())
                .setParameter("version", agreement.getVersion()));
    }

    public List<Agreement> findMatchingDeletedAgreements(Agreement agreement) {
        return getList(entityManager
                .createQuery("select a from Agreement a " +
                        "where a.validFrom = :validFrom " +
                        "and a.version = :version " +
                        "and a.id != :id " +
                        "and a.deleted = true", entity())
                .setParameter("validFrom", agreement.getValidFrom())
                .setParameter("version", agreement.getVersion())
                .setParameter("id", agreement.getId()));
    }

    public void updateUserAgreementsForUsersWhoAgreedToPreviousVersion(List<Agreement> previousAgreements, Agreement newAgreement) {
        List<Long> previousIds = previousAgreements.stream().map(Agreement::getId).collect(Collectors.toList());
        entityManager.createNativeQuery("" +
                "INSERT INTO User_Agreement (user, agreement)\n" +
                "SELECT\n" +
                "  DISTINCT user,\n" +
                "  :newId\n" +
                "FROM User_Agreement\n" +
                "WHERE user NOT IN (SELECT user\n" +
                "               FROM User_Agreement\n" +
                "               WHERE agreement = :newId)\n" +
                "      AND agreement IN (:previousIds)")
                .setParameter("newId", newAgreement.getId())
                .setParameter("previousIds", previousIds)
                .executeUpdate();
    }
}
