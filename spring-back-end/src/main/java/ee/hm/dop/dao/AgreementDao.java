package ee.hm.dop.dao;

import ee.hm.dop.model.Agreement;
import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AgreementDao extends AbstractDao<Agreement> {

    public Agreement findLatestAgreement() {
        return getSingleResult(entityManager
                .createQuery("select a from Agreement a " +
                        "where a.validFrom < :validFrom " +
                        "and a.deleted = false " +
                        "order by a.validFrom desc, a.id desc", entity())
                .setParameter("validFrom", LocalDateTime.now())
                .setMaxResults(1));
    }

    public Agreement findMatchingDeletedAgreement(Agreement agreement) {
        return getSingleResult(entityManager
                .createQuery("select a from Agreement a " +
                        "where a.validFrom = :validFrom " +
                        "and a.version = :version " +
                        "and a.id != :id " +
                        "and a.deleted = true " +
                        "order by a.id desc", entity())
                .setParameter("validFrom", agreement.getValidFrom())
                .setParameter("version", agreement.getVersion())
                .setParameter("id", agreement.getId())
                .setMaxResults(1));
    }

    public void updateUserAgreementsForUsersWhoAgreedToPreviousVersion(Agreement previousAgreement, Agreement newAgreement) {
        entityManager.createNativeQuery("" +
                "INSERT INTO User_Agreement (user, agreement, agreed, createdAt)\n" +
                "SELECT\n" +
                "  ua.user,\n" +
                "  :newId,\n" +
                "  ua.agreed,\n" +
                "  ua.createdAt\n" +
                "FROM User_Agreement ua\n" +
                "WHERE ua.user NOT IN (SELECT user\n" +
                "               FROM User_Agreement\n" +
                "               WHERE agreed = TRUE\n" +
                "               AND agreement = :newId)\n" +
                "      AND ua.agreed = TRUE \n" +
                "      AND ua.agreement = :previousId ")
                .setParameter("newId", newAgreement.getId())
                .setParameter("previousId", previousAgreement.getId())
                .executeUpdate();
    }
}
