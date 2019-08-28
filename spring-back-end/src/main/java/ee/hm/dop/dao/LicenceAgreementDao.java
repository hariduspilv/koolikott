package ee.hm.dop.dao;

import ee.hm.dop.model.LicenceAgreement;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class LicenceAgreementDao extends AbstractDao<LicenceAgreement> {

    public LicenceAgreement findLatestAgreement() {
        return getSingleResult(entityManager
                        .createQuery("select la from LicenceAgreement la " +
                                        "where la.validFrom < :validFrom " +
                                        "and la.deleted = false " +
                                        "order by la.validFrom desc, la.id desc", entity())
                        .setParameter("validFrom", LocalDateTime.now())
                        .setMaxResults(1));
    }

}
