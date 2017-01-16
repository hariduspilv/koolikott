package ee.hm.dop.dao;

import java.util.List;

import javax.persistence.NoResultException;

import ee.hm.dop.model.Version;
import org.joda.time.DateTime;

public class VersionDAO extends BaseDAO<Version> {
    public Version getLatestVersion() {
        try {
            getEntityManager().getCriteriaBuilder().createCriteriaUpdate(Version.class);
            return createQuery("from Version ver where ver.released = (select max(released) from Version)",
                    Version.class).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void addVersion(String version) {
        Version ver = new Version();
        ver.setVersion(version);
        ver.setReleased(new DateTime());
        update(ver);
    }

    public List<Version> getAllVersions() {
        return createQuery("FROM Version ORDER BY released DESC, id DESC",
                Version.class).getResultList();
    }
}
