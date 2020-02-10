package ee.hm.dop.dao;


import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;


@Repository
public class UserInstitutionDao {
    @Inject
    private EntityManager entityManager;

    public int removeExpiredUserInstitutions(Long institutionId) {
        return entityManager
            .createNativeQuery("DELETE FROM User_Institution WHERE institution = :institutionId")
            .setParameter("institutionId", institutionId)
            .executeUpdate();
    }

}
