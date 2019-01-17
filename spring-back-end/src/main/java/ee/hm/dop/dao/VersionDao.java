package ee.hm.dop.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaUpdate;

import ee.hm.dop.model.Version;

public class VersionDao extends AbstractDao<Version> {

    public Version getLatestVersion() {
        return getSingleResult(getEntityManager()
                .createQuery("from Version ver ORDER BY released DESC", entity())
                .setMaxResults(1));
    }

    public void addVersion(Version version) {
        createOrUpdate(version);
    }

    public List<Version> getAllVersions() {
        return getEntityManager().createQuery("FROM Version ORDER BY released DESC, id DESC", entity()).getResultList();
    }
}
