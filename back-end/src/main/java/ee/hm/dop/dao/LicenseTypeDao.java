package ee.hm.dop.dao;

import ee.hm.dop.model.LicenseType;

public class LicenseTypeDao extends AbstractDao<LicenseType> {

    public LicenseType findByNameIgnoreCase(String name) {
        return getEntityManager()
                .createQuery("select e from LicenseType e " +
                        "where upper(e.name) = :name ", entity())
                .setParameter("name", name)
                .getSingleResult();
    }
}