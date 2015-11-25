package ee.hm.dop.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import ee.hm.dop.model.EducationalContext;
import ee.hm.dop.model.Taxon;

public class TaxonDAO extends BaseDAO {

    public Taxon findTaxonById(Long id) {
        TypedQuery<Taxon> findById = createQuery("FROM Taxon t WHERE t.id = :id", Taxon.class) //
                .setParameter("id", id);

        return getSingleResult(findById);
    }

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

    public Taxon findTaxonByRepoName(String name, String repoTable) {
        TypedQuery<Taxon> findByName = createQuery(
                "SELECT t.taxon FROM " + repoTable + " t WHERE t.name = :name", Taxon.class) //
                .setParameter("name", name);

        return getSingleResult(findByName);
    }
}
