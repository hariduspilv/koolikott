package ee.hm.dop.dao;

import ee.hm.dop.model.ResourceType;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class ResourceTypeDAO {

    @Inject
    private EntityManager entityManager;

    public List<ResourceType> findAll() {
        return entityManager.createQuery("from ResourceType", ResourceType.class).getResultList();
    }

    public ResourceType findResourceTypeByName(String name) {
        TypedQuery<ResourceType> findByName = entityManager.createQuery("SELECT r FROM ResourceType r WHERE r.name = :name", ResourceType.class);

        ResourceType resource = null;
        try {
            resource = findByName.setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            // ignore
        }

        return resource;
    }
}
