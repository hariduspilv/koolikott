package ee.hm.dop.dao;

import ee.hm.dop.model.Agreement;
import org.joda.time.DateTime;

public class AgreementDao extends AbstractDao<Agreement> {

    public Agreement findLatestAgreement() {
        return getSingleResult(entityManager
                .createQuery("select a from Agreement a " +
                        "where a.validFrom > :validFrom " +
                        "and a.deleted = false", entity())
                .setParameter("validFrom", DateTime.now())
                .setMaxResults(1));

    }
}
