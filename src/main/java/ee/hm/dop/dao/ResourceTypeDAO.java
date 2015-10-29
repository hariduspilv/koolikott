package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.ResourceType;

public class ResourceTypeDAO {

    @Inject
    private EntityManager entityManager;

    public ResourceType findResourceTypeByName(String name) {
        TypedQuery<ResourceType> findByName = entityManager
                .createQuery("SELECT r FROM ResourceType r WHERE r.name = :name", ResourceType.class);

        ResourceType resource = null;
        try {
            resource = findByName.setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            // ignore
        }

        return resource;
    }
}
