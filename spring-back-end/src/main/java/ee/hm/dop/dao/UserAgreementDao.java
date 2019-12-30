package ee.hm.dop.dao;

import ee.hm.dop.model.User_Agreement;
import ee.hm.dop.model.enums.TermType;
import org.springframework.stereotype.Repository;

@Repository
public class UserAgreementDao extends AbstractDao<User_Agreement> {

    private static final String TERMS_AGREEMENT_URL = "/terms";
    private static final String PERSONAL_DATA_AGREEMENT_URL = "/gdpr-process";

    public boolean agreementDoesntExist(Long userId, Long agreementId) {
        return !agreementExists(userId, agreementId);
    }

    public boolean agreementExists(Long userId, Long agreementId) {
        return getSingleResult(entityManager
                .createQuery("select ua from User_Agreement ua " +
                        "where ua.agreed = true " +
                        "and ua.user.id = :user " +
                        "and ua.agreement.id = :agreement", entity())
                .setParameter("user", userId)
                .setParameter("agreement", agreementId)
                .setMaxResults(1)) != null;
    }

    public User_Agreement getLatestTermsAgreementForUser(Long userId) {
        return getUserAgreementForUser(userId, TermType.USAGE, TERMS_AGREEMENT_URL);
    }

    public User_Agreement getLatestGdprTermsAgreementForUser(Long userId) {
        return getUserAgreementForUser(userId, TermType.GDPR, PERSONAL_DATA_AGREEMENT_URL);
    }

    private User_Agreement getUserAgreementForUser(Long userId, TermType type, String url) {
        return getSingleResult(getEntityManager().createQuery("select ua from User_Agreement ua " +
                "join Agreement a on ua.agreement = a.id " +
                "where (a.type = :type or a.url = :url)" +
                "and ua.user.id = :user order by ua.createdAt desc", entity())
                .setParameter("type", type)
                .setParameter("url", url)
                .setParameter("user", userId)
                .setMaxResults(1));
    }
}
