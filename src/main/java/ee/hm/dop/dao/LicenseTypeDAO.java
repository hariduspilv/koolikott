package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import ee.hm.dop.model.LicenseType;

public class LicenseTypeDAO {

    @Inject
    private EntityManager entityManager;

    public List<LicenseType> findAll() {
        return entityManager.createQuery("from LicenseType", LicenseType.class).getResultList();
    }

}