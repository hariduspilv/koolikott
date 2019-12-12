package ee.hm.dop.dao;

import ee.hm.dop.model.Agreement;
import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AgreementDao extends AbstractDao<Agreement> {

    private static final String TERMS_AGREEMENT_URL = "/terms";
    private static final String GDRP_TERM_AGREEMENT_URL = "/gdpr-process";

    public List<Agreement> getValidAgreements() {
        return getList(entityManager
                .createQuery("select a from Agreement a " +
                        "where a.deleted = false " +
                        "order by a.validFrom desc, a.id desc", entity()));
    }

    public Agreement findLatestTermsAgreement() {
        return findLatestAgreement(TERMS_AGREEMENT_URL);
    }

    public Agreement findLatestGdprTermsAgreement() {
        return findLatestAgreement(GDRP_TERM_AGREEMENT_URL);
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

    private Agreement findLatestAgreement(String url) {
        return getSingleResult(entityManager
                .createQuery("select a from Agreement a " +
                        "where a.url = :url " +
                        "and a.validFrom < :validFrom " +
                        "and a.deleted = false " +
                        "order by a.validFrom desc, a.id desc", entity())
                .setParameter("url", url)
                .setParameter("validFrom", LocalDateTime.now())
                .setMaxResults(1));
    }
}
