package ee.hm.dop.dao;

import ee.hm.dop.model.UserLicenceAgreement;
import org.springframework.stereotype.Repository;

@Repository
public class UserLicenceAgreementDao extends AbstractDao<UserLicenceAgreement> {

    public UserLicenceAgreement getLatestUserLicenceAgreement(Long userId) {
        return getSingleResult(entityManager
                .createQuery("select u from UserLicenceAgreement u " +
                        "where u.user.id = :id " +
                        "order by u.clickedAt desc", entity())
                .setParameter("id", userId)
                .setMaxResults(1));
    }

    public String setUserLicenceAgreement(Long userId, boolean agreed, boolean disagreed) {
        return null;
    }
}
