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
                .createQuery("select distinct i.area from InstitutionEhis i order by i.area asc", String.class)
                .getResultList();
    }

    public List<InstitutionEhis> getInstitutionPerArea(String area) {
        return getEntityManager().createQuery("" +
                "   SELECT i FROM InstitutionEhis i where i.area=:area ORDER BY i.name ASC", entity())
                .setParameter("area", area)
                .getResultList();
    }
}
