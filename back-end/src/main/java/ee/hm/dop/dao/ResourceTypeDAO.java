package ee.hm.dop.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import ee.hm.dop.model.ResourceType;

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
        List<ResourceType> resultList = entityManager.createQuery("select r FROM ResourceType r", ResourceType.class)
                .getResultList();

        return resultList;
    }

    public List<ResourceType> findUsedResourceTypes() {
        Query q = entityManager.createNativeQuery("SELECT * FROM ResourceType r " +
                "WHERE r.id IN " +
                "(SELECT DISTINCT resourceType FROM Material_ResourceType as rt " +
                "INNER JOIN LearningObject as lo where lo.id=rt.material AND lo.deleted=false)", ResourceType.class);

        return q.getResultList();
    }
}
