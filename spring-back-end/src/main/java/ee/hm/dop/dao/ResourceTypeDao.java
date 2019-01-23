package ee.hm.dop.dao;

import ee.hm.dop.model.ResourceType;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class ResourceTypeDao extends AbstractDao<ResourceType>{

    public List<ResourceType> findUsedResourceTypes() {
        return (List<ResourceType>) entityManager.createQuery("SELECT DISTINCT mr.resourceTypes FROM Material mr where mr.deleted = false").getResultList();
    }
}
