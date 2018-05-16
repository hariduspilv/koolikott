package ee.hm.dop.dao;

import ee.hm.dop.model.Agreement;
import org.joda.time.DateTime;

import java.util.List;

public class AgreementDao extends AbstractDao<Agreement> {

    public Agreement findLatestAgreement() {
        return getSingleResult(entityManager
                .createQuery("select a from Agreement a " +
                        "where a.validFrom < :validFrom " +
                        "and a.deleted = false", entity())
                .setParameter("validFrom", DateTime.now())
                .setMaxResults(1));

    }

    public List<Agreement> findMatchingAgreements(Agreement agreement) {
        return getList(entityManager
                .createQuery("select a from Agreement a " +
                        "where a.validFrom = :validFrom " +
                        "and a.version = :version " +
                        "and a.id != :id " +
                        "and a.deleted = true", entity())
                .setParameter("validFrom", agreement.getValidFrom())
                .setParameter("version", agreement.getVersion())
                .setParameter("id", agreement.getId()));
    }

    public void updateUserAgreementsForUsersWhoAgreedToPreviousVersion() {
        //todo
    }
}
