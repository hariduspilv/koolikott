package ee.hm.dop.dao;

import ee.hm.dop.model.User_Agreement;
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
        return getUserAgreementForUser(userId, TERMS_AGREEMENT_URL);
    }

    public User_Agreement getLatestPersonalDataAgreementForUser(Long userId) {
        return getUserAgreementForUser(userId, PERSONAL_DATA_AGREEMENT_URL);
    }

    private User_Agreement getUserAgreementForUser(Long userId, String url) {
        return getSingleResult(getEntityManager().createQuery("select ua from User_Agreement ua " +
                "join Agreement a on ua.agreement = a.id " +
                "where a.url = :url " +
                "and ua.user.id = :user order by ua.createdAt desc", entity())
                .setParameter("user", userId)
                .setParameter("url", url)
                .setMaxResults(1));
    }
}
