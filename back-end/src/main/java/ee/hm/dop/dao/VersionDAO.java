package ee.hm.dop.dao;

import java.util.List;

import javax.persistence.NoResultException;

import ee.hm.dop.model.Version;

public class VersionDAO extends BaseDAO<Version> {
    public Version getLatestVersion() {
        try {
            getEntityManager().getCriteriaBuilder().createCriteriaUpdate(Version.class);
            return createQuery("from Version ver ORDER BY released DESC",
                    Version.class).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void addVersion(Version version) {
        update(version);
    }

    public List<Version> getAllVersions() {
        return createQuery("FROM Version ORDER BY released DESC, id DESC",
                Version.class).getResultList();
    }
}
