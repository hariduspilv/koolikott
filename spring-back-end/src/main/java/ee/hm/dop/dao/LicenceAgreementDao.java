package ee.hm.dop.dao;

import ee.hm.dop.model.LicenceAgreement;
import org.springframework.stereotype.Repository;

@Repository
public class LicenceAgreementDao extends AbstractDao<LicenceAgreement> {

    public LicenceAgreement findLatestAgreement() {
        return getSingleResult(entityManager
        .createQuery("select la from LicenceAgreement la " +
                "where la.deleted = false " +
                "order by la.validFrom desc, la.id desc", entity()));
    }

}
