package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import ee.hm.dop.model.ResourceType;

public class ResourceTypeDAO {

    @Inject
    private EntityManager entityManager;

    public List<ResourceType> findAll() {
        return entityManager.createQuery("from ResourceType", ResourceType.class).getResultList();
    }

}
