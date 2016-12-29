package ee.hm.dop.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;

public class TaxonDAO extends BaseDAO<Taxon> {

    public Taxon findTaxonById(Long id) {
        TypedQuery<Taxon> findById = createQuery("FROM Taxon t WHERE t.id = :id", Taxon.class) //
                .setParameter("id", id);

        return getSingleResult(findById);
    }

    public EducationalContext findEducationalContextByName(String name) {
        TypedQuery<Taxon> findByName = createQuery(
                "FROM Taxon t WHERE lower(t.name) = :name and level = 'EDUCATIONAL_CONTEXT'", Taxon.class) //
                .setParameter("name", name.toLowerCase());

        return (EducationalContext) getSingleResult(findByName);
    }

    public List<EducationalContext> findAllEducationalContext() {
        return (List<EducationalContext>) (List<?>) getEntityManager()
                .createNamedQuery("findAllEducationalContext", Taxon.class)
                .getResultList();
    }

    public Taxon findTaxonByName(String name) {
        Taxon taxon = null;
        try {
            taxon = createQuery("SELECT t FROM Taxon t WHERE lower(t.name)=:name", Taxon.class)
                    .setParameter("name", name.toLowerCase())
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
        }

        return taxon;
    }

    public Taxon findTaxonByRepoName(String name, String repoTable, Class<? extends Taxon> level) {
        List<Taxon> taxons = createQuery("SELECT t.taxon FROM " + repoTable + " t WHERE lower(t.name) = :name",
                Taxon.class).setParameter("name", name.toLowerCase()).getResultList();
        List<Taxon> res = taxons.stream().filter(t -> level.isAssignableFrom(t.getClass()))
                .collect(Collectors.toList());

        if (res != null && !res.isEmpty()) {
            return res.get(0);
        }

        return null;
    }
}
