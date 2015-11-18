package ee.hm.dop.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import ee.hm.dop.model.EducationalContext;
import ee.hm.dop.model.Taxon;

public class TaxonDAO extends BaseDAO {

    public EducationalContext findEducationalContextByName(String name) {
        TypedQuery<Taxon> findByName = createQuery(
                "FROM Taxon t WHERE t.name = :name and level = 'EDUCATIONAL_CONTEXT'", Taxon.class) //
                        .setParameter("name", name);

        return (EducationalContext) getSingleResult(findByName);
    }

    public List<EducationalContext> findAllEducationalContext() {
        List<Taxon> resultList = createQuery("FROM Taxon t WHERE level = 'EDUCATIONAL_CONTEXT'", Taxon.class)
                .getResultList();

        List<EducationalContext> educationalContexts = new ArrayList<>();

        for (Taxon taxon : resultList) {
            educationalContexts.add((EducationalContext) taxon);
        }

        return educationalContexts;
    }

}
