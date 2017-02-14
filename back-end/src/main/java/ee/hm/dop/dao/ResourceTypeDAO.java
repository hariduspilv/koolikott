package ee.hm.dop.dao;

import ee.hm.dop.model.ResourceType;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class ResourceTypeDAO {

    @Inject
    private EntityManager entityManager;

    public ResourceType findResourceTypeByName(String name) {
        TypedQuery<ResourceType> findByName = entityManager.createQuery(
                "SELECT r FROM ResourceType r WHERE r.name = :name", ResourceType.class);

        ResourceType resource = null;
        try {
            resource = findByName.setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            // ignore
        }

        return resource;
    }

    public List<ResourceType> findAllResourceTypes() {
        return entityManager.createQuery("select r FROM ResourceType r", ResourceType.class)
                .getResultList();
    }

    public List<ResourceType> findUsedResourceTypes() {
        return (List<ResourceType>) entityManager.createQuery("SELECT DISTINCT mr.resourceTypes FROM Material mr where mr.deleted = false").getResultList();
    }
}
