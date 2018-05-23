package ee.hm.dop.dao;

import ee.hm.dop.model.User_Agreement;

public class UserAgreementDao extends AbstractDao<User_Agreement> {

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
}
