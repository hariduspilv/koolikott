package ee.hm.dop.dao;

import ee.hm.dop.model.ehis.InstitutionEhis;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InstitutionEhisDao extends AbstractDao<InstitutionEhis> {

    @Override
    public InstitutionEhis createOrUpdate(InstitutionEhis entity) {
        return super.createOrUpdate(entity);
    }

    public List<String> getInstitutionAreas() {
        return getEntityManager()
                .createQuery("SELECT DISTINCT i.area FROM InstitutionEhis i ORDER BY i.area ASC", String.class)
                .getResultList();
    }

    public List<InstitutionEhis> getInstitutionPerArea(String area) {
        return getEntityManager().createQuery("" +
                "   SELECT i FROM InstitutionEhis i WHERE i.area=:area ORDER BY i.name ASC", entity())
                .setParameter("area", area)
                .getResultList();
    }
}
