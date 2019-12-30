package ee.hm.dop.dao;

import ee.hm.dop.model.Agreement;
import ee.hm.dop.model.enums.TermType;
import org.springframework.stereotype.Repository;


@Repository
public class AgreementDao extends AbstractDao<Agreement> {

    private static final String TERMS_AGREEMENT_URL = "/terms";
    private static final String GDRP_TERM_AGREEMENT_URL = "/gdpr-process";

    public Agreement findLatestTermsAgreement() {
        return findLatestAgreement(TermType.USAGE, TERMS_AGREEMENT_URL);
    }

    public Agreement findLatestGdprTermsAgreement() {
        return findLatestAgreement(TermType.GDPR, GDRP_TERM_AGREEMENT_URL);
    }

    public Agreement findMatchingDeletedAgreement(Agreement agreement) {
        return getSingleResult(entityManager
                .createQuery("select a from Agreement a " +
                        "where a.id <> :id " +
                        "and a.deleted = true " +
                        "order by a.id desc", entity())
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

    private Agreement findLatestAgreement(TermType type, String url) {
        return getSingleResult(entityManager
                .createQuery("select a from Agreement a " +
                        "where (a.type = :type or a.url = :url)" +
                        "and a.deleted = false " +
                        "order by a.createdAt desc, a.id desc", entity())
                .setParameter("type", type)
                .setParameter("url", url)
                .setMaxResults(1));
    }
}
